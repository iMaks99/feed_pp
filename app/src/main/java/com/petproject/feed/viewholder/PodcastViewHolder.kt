package com.petproject.feed.viewholder

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.feed_podcast_item.view.*

class PodcastViewHolder(view: View, private var parentFragment: Fragment) :
    RecyclerView.ViewHolder(view) {

    fun bind(feed: NewsFeedForListDto?, adapter: FeedListAdapter, position: Int) {

        val mainActivity = (parentFragment.activity as MainActivity)

        val width = (itemView.context.resources.displayMetrics.widthPixels - dpToPx(
            32f,
            itemView.resources
        ))
        val params = itemView.layoutParams as RecyclerView.LayoutParams
        params.height = width
        itemView.layoutParams = params

        val podcastItem = feed!!.getNewsFeedItem() as PodcastItem

        itemView.PodcastItemTitleTextView.text = podcastItem.title
        itemView.PodcastItemDurationTextView.text = "${podcastItem.duration / 60} мин"

        itemView.PodcastItemPubTimeTextView.text = DateUtils.getRelativeTimeSpanString(
            toLocalTime(podcastItem.publicationTime * 1000),
            System.currentTimeMillis(),
            DateUtils.SECOND_IN_MILLIS
        )

        itemView.PodcastItemNumberTextView.text = "Выпуск #${podcastItem.number}"

        itemView.PodcastItemPlayImageView.setOnClickListener {
            if (itemView.PodcastItemPlayImageView.tag == "play") {
                itemView.PodcastItemPlayImageView.setImageResource(R.drawable.ic_media_pause)
                itemView.PodcastItemPlayImageView.tag = "pause"
                (parentFragment.activity as MainActivity).podcastMiniPlayerBottomSheetBehavior?.state =
                    BottomSheetBehavior.STATE_EXPANDED
                (parentFragment.activity as MainActivity).initPodcast(podcastItem)
            } else {
                itemView.PodcastItemPlayImageView.setImageResource(R.drawable.ic_media_play)
                itemView.PodcastItemPlayImageView.tag = "play"
                (parentFragment.activity as MainActivity).podcastPlayerService?.pausePodcast()
            }

            if (mainActivity.podcastPlayerService != null && mainActivity.playerPlaySubscriber != null) {
                val disposable =
                    mainActivity.playerPlaySubscriber!!.subscribe {
                        when (it) {
                            PlaybackStatus.PLAYING -> {
                                itemView.PodcastItemPlayImageView.setImageResource(R.drawable.ic_media_pause)
                                itemView.PodcastItemPlayImageView.tag = "pause"
                            }

                            PlaybackStatus.PAUSED -> {
                                itemView.PodcastItemPlayImageView.setImageResource(R.drawable.ic_media_play)
                                itemView.PodcastItemPlayImageView.tag = "play"
                            }
                        }
                    }
                adapter.setNewSubscriber(disposable, position)
            }
        }

        itemView.setOnClickListener {
            (parentFragment.activity as MainActivity).podcastPlayerBottomSheetBehavior?.state =
                BottomSheetBehavior.STATE_EXPANDED
            (parentFragment.activity as MainActivity).podcastMiniPlayerBottomSheetBehavior?.state =
                BottomSheetBehavior.STATE_EXPANDED

            adapter.setNewSubscriber(mainActivity.playerPlaySubscriber!!.subscribe {
                when (it) {
                    PlaybackStatus.PLAYING -> {
                        itemView.PodcastItemPlayImageView.setImageResource(R.drawable.ic_media_pause)
                        itemView.PodcastItemPlayImageView.tag = "pause"
                    }

                    PlaybackStatus.PAUSED -> {
                        itemView.PodcastItemPlayImageView.setImageResource(R.drawable.ic_media_play)
                        itemView.PodcastItemPlayImageView.tag = "play"
                    }
                }
            }, position)
            itemView.PodcastItemPlayImageView.setImageResource(R.drawable.ic_media_pause)
            itemView.PodcastItemPlayImageView.tag = "pause"
            (parentFragment.activity as MainActivity).initPodcast(podcastItem)
        }

        Glide.with(itemView).load(podcastItem.imageUrl)
            .into(itemView.PodcastItemThumbnailImageView)
    }

    companion object {
        fun create(parent: ViewGroup, parentFragment: Fragment): PodcastViewHolder {
            return PodcastViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.feed_podcast_item, parent, false),
                parentFragment
            )
        }
    }
}