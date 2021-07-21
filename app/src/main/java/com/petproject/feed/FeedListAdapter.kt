package com.petproject.feed

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.feed_podcast_item.view.*

class FeedListAdapter(
    val parentFragment: Fragment,
    val recyclerView: RecyclerView
) :
    PagedListAdapter<NewsFeedForListDto, RecyclerView.ViewHolder>(FeedDiffCallback) {

    private var state = State.LOADING

    private var pref = MainActivity.pref
    var currentPodcastPosition: Int = -1
    var currentPodcastSubscriber: Disposable? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            FeedViewType.WP_POST.viewType -> WpPostViewHolder.create(parent)
            FeedViewType.PODCAST.viewType -> PodcastViewHolder.create(parent, parentFragment)
            FeedViewType.VIDEO.viewType -> VideoPostViewHolder.create(parent)
            FeedViewType.EXCLUSIVE_FEED.viewType -> ExclusivePostViewHolder.create(parent)
            FeedViewType.PRODUCT_SELECTION.viewType -> ProductSelectionPostViewHolder.create(parent)
            FeedViewType.INSTAGRAM_POST.viewType -> InstagramPostViewHolder.create(
                parent.context,
                parent
            )
            FeedViewType.RATE_APP.viewType -> RateAppViewHolder.create(parent)
            FeedViewType.LIVE.viewType -> LivePostViewHolder.create(parent)
            FeedViewType.COMPETITION.viewType -> CompetitionItemViewHolder.create(parent)
            else -> NoneViewHolder.create(parent)
        }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty() && holder is PodcastViewHolder) {
            when (payloads[0]) {
                PlaybackStatus.STOPPED -> {
                    holder.itemView.PodcastItemPlayImageView.setImageResource(R.drawable.ic_media_play)
                    holder.itemView.PodcastItemPlayImageView.tag = "play"
                }
            }
        } else
            super.onBindViewHolder(holder, position, payloads)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position == itemCount - 1) {
            val params = holder.itemView.layoutParams as RecyclerView.LayoutParams
            params.bottomMargin = dpToPx(20f, holder.itemView.context.resources)
            holder.itemView.layoutParams = params
        }

        when (super.getItem(position)!!.itemType) {
            ItemType.WP_POST -> (holder as WpPostViewHolder).bind(getItem(position))
            ItemType.PODCAST -> (holder as PodcastViewHolder).bind(getItem(position), this, position)
            ItemType.VIDEO -> (holder as VideoPostViewHolder).bind(
                getItem(position),
                position
            )
            ItemType.EXCLUSIVE_FEED -> (holder as ExclusivePostViewHolder).bind(getItem(position))
            ItemType.PRODUCT_SELECTION -> (holder as ProductSelectionPostViewHolder).bind(
                getItem(position)
            )
            ItemType.INSTAGRAM_POST -> (holder as InstagramPostViewHolder).bind(getItem(position))
            ItemType.RATE_APP -> (holder as RateAppViewHolder).bind(this)
            ItemType.LIVE_STREAM -> (holder as LivePostViewHolder).bind(getItem(position))
            ItemType.COMPETITION -> (holder as CompetitionItemViewHolder).bind(getItem(position)!!.getNewsFeedItem() as CompetitionForMobileListDto)
            else -> (holder as NoneViewHolder).bind()
        }
    }

    override fun getItemViewType(position: Int): Int =
        when (super.getItem(position)!!.itemType) {
            ItemType.WP_POST -> FeedViewType.WP_POST.viewType
            ItemType.PODCAST -> FeedViewType.PODCAST.viewType
            ItemType.VIDEO -> FeedViewType.VIDEO.viewType
            ItemType.EXCLUSIVE_FEED -> FeedViewType.EXCLUSIVE_FEED.viewType
            ItemType.PRODUCT_SELECTION -> FeedViewType.PRODUCT_SELECTION.viewType
            ItemType.INSTAGRAM_POST -> FeedViewType.INSTAGRAM_POST.viewType
            ItemType.RATE_APP -> FeedViewType.RATE_APP.viewType
            ItemType.TWITTER_POST -> FeedViewType.TWITTER_POST.viewType
            ItemType.COMPETITION -> FeedViewType.COMPETITION.viewType
            ItemType.LIVE_STREAM -> FeedViewType.LIVE.viewType
            else -> FeedViewType.NONE.viewType
        }

    fun setState(state: State) {
        this.state = state
        notifyDataSetChanged()
    }

    companion object {
        val FeedDiffCallback = object : DiffUtil.ItemCallback<NewsFeedForListDto>() {
            override fun areItemsTheSame(
                oldItem: NewsFeedForListDto,
                newItem: NewsFeedForListDto
            ): Boolean {
                val oldFeedItem = oldItem.getNewsFeedItem()
                val newFeedItem = newItem.getNewsFeedItem()

                val old: Long = when (oldFeedItem) {
                    is WpPostItem -> oldFeedItem.id
                    is PodcastItem -> oldFeedItem.id
                    is InstagramPostItem -> oldFeedItem.id
                    is ExclusiveFeedItem -> oldFeedItem.id
                    is VideoPostItem -> oldFeedItem.id
                    is ProductSelectionPostItem -> oldFeedItem.id
                    is RateAppPostItem -> 1
                    is CompetitionForMobileListDto -> oldFeedItem.id.toLong()
                    is LivePostItem -> oldFeedItem.id
                    is NotAFeed -> -1
                }
                val new = when (newFeedItem) {
                    is WpPostItem -> newFeedItem.id
                    is PodcastItem -> newFeedItem.id
                    is InstagramPostItem -> newFeedItem.id
                    is ExclusiveFeedItem -> newFeedItem.id
                    is VideoPostItem -> newFeedItem.id
                    is ProductSelectionPostItem -> newFeedItem.id
                    is RateAppPostItem -> 1
                    is CompetitionForMobileListDto -> newFeedItem.id.toLong()
                    is LivePostItem -> newFeedItem.id
                    is NotAFeed -> -1
                }

                return old == new
            }

            override fun areContentsTheSame(
                oldItem: NewsFeedForListDto,
                newItem: NewsFeedForListDto
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

    fun setNewSubscriber(subscription: Disposable, position: Int) {

        if(currentPodcastPosition == position) return

        currentPodcastSubscriber?.dispose()
        currentPodcastSubscriber = subscription

        if (currentPodcastPosition != position)
            recyclerView.post {
                notifyItemChanged(currentPodcastPosition, PlaybackStatus.STOPPED)
                currentPodcastPosition = position
            }
    }
}

enum class FeedViewType(val viewType: Int) {
    WP_POST(1),
    VIDEO(2),
    PODCAST(3),
    EXCLUSIVE_FEED(4),
    PRODUCT_SELECTION(5),
    INSTAGRAM_POST(6),
    TWITTER_POST(7),
    RATE_APP(8),
    COMPETITION(9),
    LIVE(10),
    NONE(0)
}