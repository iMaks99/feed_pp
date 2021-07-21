package com.petproject.feed.viewholder

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.feed_exclusive_post_item.view.*

class ExclusivePostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(feed: NewsFeedForListDto?) {

        val width = (itemView.context.resources.displayMetrics.widthPixels - dpToPx(32f, itemView.resources))
        val params = itemView.layoutParams as RecyclerView.LayoutParams
        params.height = (1.12 * width).toInt()
        itemView.layoutParams = params

        val exclusiveFeedItem = feed!!.getNewsFeedItem() as ExclusiveFeedItem

        itemView.ExclusivePostItemTitleTextView.text = exclusiveFeedItem.title
        itemView.ExclusivePostItemTagTextView.text = exclusiveFeedItem.tag

        Glide.with(itemView).load(exclusiveFeedItem.fileName)
            .into(itemView.ExclusivePostItemThumbnailImageView)

        itemView.setOnClickListener {
            val intent = Intent(itemView.context, ExclusivePostActivity::class.java)
            intent.putExtra("id", exclusiveFeedItem.id)
            intent.putExtra("isBookmark", false)

            itemView.context.startActivity(intent)
        }
    }

    companion object {
        fun create(parent: ViewGroup): ExclusivePostViewHolder {
            return ExclusivePostViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.feed_exclusive_post_item, parent, false)
            )
        }
    }
}