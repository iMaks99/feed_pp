package com.petproject.feed.viewholder

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.feed_live_post_item.view.*
import java.text.SimpleDateFormat
import java.util.*

class LivePostViewHolder(itemView: View) :
    RecyclerView.ViewHolder(itemView) {

    fun bind(live: NewsFeedForListDto?) {

        if (live != null && live.getNewsFeedItem() is LivePostItem) {
            val livePost = live.getNewsFeedItem() as LivePostItem

            itemView.liveFeedItemTitleTextView.text = livePost.title
            itemView.liveFeedItemSubitleTextView.text = livePost.description

            when (livePost.status) {
                LiveStatus.PLANNED -> {

                    itemView.liveFeedItemTitleTextView.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.feed_live_text_color
                        )
                    )
                    itemView.liveFeedItemSubitleTextView.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.feed_live_text_color
                        )
                    )
                    itemView.liveFeedItemIconImageView.setColorFilter(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.feed_live_waiting_icon_tint
                        )
                    )
                    itemView.liveFeedCardView.setCardBackgroundColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.feed_live_background
                        )
                    )

                    val dateFormat = SimpleDateFormat("d MMMM в HH:mm", Locale.getDefault())
                    itemView.liveFeedItemWatchButton.text = dateFormat.format(livePost.liveStart * 1000)

                    itemView.liveFeedItemWatchButton.isEnabled = false
                    itemView.liveFeedItemWatchButton.background = ContextCompat.getDrawable(
                        itemView.context,
                        R.drawable.feed_live_item_waiting_button_style
                    )
                    itemView.liveFeedItemWatchButton.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.feed_live_waiting_btn_text
                        )
                    )
                }

                LiveStatus.LIVE -> {
                    itemView.liveFeedItemTitleTextView.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.feed_live_text_color_streaming
                        )
                    )
                    itemView.liveFeedItemSubitleTextView.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.feed_live_text_color_streaming
                        )
                    )
                    itemView.liveFeedItemIconImageView.setColorFilter(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.feed_live_stream_icon_tint
                        )
                    )
                    itemView.liveFeedCardView.setCardBackgroundColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.feed_live_background_streaming
                        )
                    )

                    itemView.liveFeedItemWatchButton.isEnabled = true
                    itemView.liveFeedItemWatchButton.text =
                        itemView.context.getString(R.string.feed_live_button_title)
                    itemView.liveFeedItemWatchButton.background = ContextCompat.getDrawable(
                        itemView.context,
                        R.drawable.feed_live_item_stream_button_style
                    )
                    itemView.liveFeedItemWatchButton.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.feed_live_stream_btn_text
                        )
                    )

                    itemView.liveFeedItemWatchButton.setOnClickListener {
                        val intent = Intent(itemView.context, LiveActivity::class.java)
                        intent.putExtra("live", livePost)
                        itemView.context.startActivity(intent)
                    }

                    itemView.setOnClickListener {
                        val intent = Intent(itemView.context, LiveActivity::class.java)
                        intent.putExtra("live", livePost)
                        itemView.context.startActivity(intent)
                    }
                }

                LiveStatus.FINISHED -> {
                    itemView.liveFeedItemIconImageView.visibility = View.INVISIBLE
                    itemView.liveFeedItemTitleTextView.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.feed_live_text_color
                        )
                    )
                    itemView.liveFeedItemSubitleTextView.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.feed_live_text_color
                        )
                    )
                    itemView.liveFeedCardView.setCardBackgroundColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.feed_live_background
                        )
                    )

                    itemView.liveFeedItemWatchButton.isEnabled = true
                    itemView.liveFeedItemWatchButton.text =
                        itemView.context.getString(R.string.feed_live_button_ended)
                    itemView.liveFeedItemWatchButton.background = ContextCompat.getDrawable(
                        itemView.context,
                        R.drawable.feed_live_item_ended_button_style
                    )
                    itemView.liveFeedItemWatchButton.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.feed_live_ended_btn_text
                        )
                    )

                    itemView.liveFeedItemWatchButton.setOnClickListener {
                        val intent = Intent(itemView.context, LiveActivity::class.java)
                        intent.putExtra("live", livePost)
                        itemView.context.startActivity(intent)
                    }

                    itemView.setOnClickListener {
                        val intent = Intent(itemView.context, LiveActivity::class.java)
                        intent.putExtra("live", livePost)
                        itemView.context.startActivity(intent)
                    }
                }
            }
        } else {
            itemView.visibility = View.GONE
            val params = RecyclerView.LayoutParams(0, 0)
            params.setMargins(0, 0, 0, 0)
            itemView.layoutParams = params
        }
    }

    companion object {
        fun create(parent: ViewGroup): LivePostViewHolder {
            return LivePostViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.feed_live_post_item, parent, false)
            )
        }
    }
}