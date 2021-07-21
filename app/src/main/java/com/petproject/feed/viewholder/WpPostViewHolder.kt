package com.petproject.feed.viewholder

import android.content.Intent
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.feed_wp_post_item.view.*


class WpPostViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    fun bind(feed: NewsFeedForListDto?) {

        val width = (itemView.context.resources.displayMetrics.widthPixels - dpToPx(
            32f,
            itemView.resources
        ))
        val params = itemView.layoutParams as RecyclerView.LayoutParams
        params.height = (1.12 * width).toInt()
        itemView.layoutParams = params

        val wpPostItem = feed!!.getNewsFeedItem() as WpPostItem

        itemView.WpPostItemPostTitleTextView.text = wpPostItem.postTitle

        if (wpPostItem.secondaryTitle.isNotEmpty())
            itemView.WpPostItemSecondaryTitleTextView.text = wpPostItem.secondaryTitle
        else {
            itemView.WpPostItemSecondaryTitleTextView.visibility = View.GONE

            val titleParams =
                itemView.WpPostItemPostTitleTextView.layoutParams as ConstraintLayout.LayoutParams
            titleParams.bottomMargin = 0
            titleParams.goneBottomMargin = 0
            itemView.WpPostItemPostTitleTextView.layoutParams = titleParams
        }

        itemView.WpPostItemCategoryTextView.text = wpPostItem.category

        itemView.WpPostItemPubDateTextView.text = DateUtils.getRelativeTimeSpanString(
            toLocalTime(wpPostItem.publicationTime),
            System.currentTimeMillis(),
            DateUtils.SECOND_IN_MILLIS
        )

        Glide.with(itemView).load(wpPostItem.thumbnailUrl)
            .into(itemView.WpPostItemThumbnailImageView)

        itemView.setOnClickListener {
            val intent = Intent(itemView.context, WpPostActivity::class.java)
            intent.putExtra("wpPostId", wpPostItem.wpPostId.toInt())
            intent.putExtra("id", wpPostItem.id.toInt())

            val options = ActivityOptionsCompat.makeScaleUpAnimation(
                view, 0, 0, view.width, view.height
            ).toBundle()

            itemView.context.startActivity(intent, options)
        }

        val thumbScaleInAnimation = ScaleAnimation(
            1f,
            1.05f,
            1f,
            1.05f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        thumbScaleInAnimation.duration = 750
        thumbScaleInAnimation.fillAfter = true

        val thumbScaleOutAnimation = ScaleAnimation(
            1.05f,
            1f,
            1.05f,
            1f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        thumbScaleOutAnimation.duration = 500
        thumbScaleOutAnimation.fillAfter = true

        itemView.WpPostItemContainer.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    itemView.WpPostItemThumbnailImageView.startAnimation(thumbScaleInAnimation)
                    true
                }

                MotionEvent.ACTION_UP -> {
                    itemView.WpPostItemThumbnailImageView.startAnimation(thumbScaleOutAnimation)
                    true
                }

                MotionEvent.ACTION_CANCEL -> {
                    itemView.WpPostItemThumbnailImageView.startAnimation(thumbScaleOutAnimation)
                    true
                }

                else -> false
            }

            false
        }
    }

    companion object {
        fun create(parent: ViewGroup): WpPostViewHolder {
            return WpPostViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.feed_wp_post_item, parent, false)
            )
        }
    }
}