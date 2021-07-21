package com.petproject.feed.viewholder

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.android.synthetic.main.feed_video_post_item.view.*

class VideoPostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(feed: NewsFeedForListDto?, position: Int) {

        val params = itemView.VideoPostPreviewRelativeLayout.layoutParams as LinearLayout.LayoutParams
        params.height = (0.58 * itemView.context.resources.displayMetrics.widthPixels - dpToPx(32f, itemView.resources)).toInt()
        itemView.VideoPostPreviewRelativeLayout.layoutParams = params

        val videoPostItem = feed!!.getNewsFeedItem() as VideoPostItem

        itemView.VideoPostItemPostTitleTextView.text = videoPostItem.title
        itemView.VideoPostItemPubTypeTextView.text = "Новое видео"
        itemView.VideoPostItemChannelTitleTextView.text = videoPostItem.username

        itemView.VideoPostItemPubDateTextView.text = DateUtils.getRelativeTimeSpanString(
            videoPostItem.publicationTime,
            System.currentTimeMillis(),
            DateUtils.SECOND_IN_MILLIS
        )

        itemView.setOnClickListener {
            val intent = Intent(itemView.context, VideoActivity::class.java)
            intent.putExtra("video", videoPostItem)
            itemView.context.startActivity(intent)
        }

        Glide.with(itemView)
            .asBitmap()
            .load(videoPostItem.imageUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {

                    itemView.VideoPostPreviewRelativeLayout.requestLayout()

                    itemView.VideoPostPreviewRelativeLayout.layoutParams.height =
                        (itemView.context.resources.displayMetrics.widthPixels - dpToPx(
                            40f,
                            itemView.context.resources
                        )) * 9 / 16

                    itemView.VideoPostItemThumbnailImageView.setImageBitmap(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })

        Glide.with(itemView).load(videoPostItem.channelAvatarUrl)
            .apply(RequestOptions.circleCropTransform())
            .into(itemView.VideoPostItemChannelAvatarImageView)
    }

    companion object {
        fun create(parent: ViewGroup): VideoPostViewHolder {
            return VideoPostViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.feed_video_post_item, parent, false)
            )
        }
    }
}