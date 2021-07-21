package com.petproject.feed.viewholder

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.feed_instagram_post_item.view.*


class InstagramPostViewHolder(var context: Context, itemView: View) :
    RecyclerView.ViewHolder(itemView) {

    fun bind(feed: NewsFeedForListDto?) {
        val instagramPostItem = feed!!.getNewsFeedItem() as InstagramPostItem

        itemView.InstagramPostItemUserNameTextView.text = instagramPostItem.username
        itemView.InstagramPostItemTitleTextView.text = instagramPostItem.title

        itemView.InstagramPostItemPubDateTextView.text = DateUtils.getRelativeTimeSpanString(
            instagramPostItem.publicationTime,
            System.currentTimeMillis(),
            DateUtils.MINUTE_IN_MILLIS
        )

        Glide.with(itemView).load(instagramPostItem.authorImageUrl)
            .apply(RequestOptions.circleCropTransform())
            .into(itemView.InstagramPostItemUserAvatarImageView)


        itemView.InstagramPostItemPhotoViewPager.adapter =
            InstagramPostSliderAdapter(
                context,
                itemView.relativeLayout,
                instagramPostItem.mediaList
            )

        if (instagramPostItem.mediaList.size > 1) {
            itemView.InstagramPostItemPhotoIndicatorTabLayout.setupWithViewPager(
                itemView.InstagramPostItemPhotoViewPager,
                true
            )

            itemView.InstagramPostItemPhotoIndicatorTabLayout.visibility = View.VISIBLE
        }

        itemView.InstagramPostItemLinkImageView.setOnClickListener {
            openInstagram(Uri.parse(instagramPostItem.url))
        }

        itemView.InstagramPostItemLogoImageView.setOnClickListener {
            openInstagram(Uri.parse(instagramPostItem.url))
        }
    }

    private fun openInstagram(uri: Uri) {
        val likeIng = Intent(Intent.ACTION_VIEW, uri)

        likeIng.setPackage("com.instagram.android")

        try {
            itemView.context.startActivity(likeIng)
        } catch (e: ActivityNotFoundException) {
            itemView.context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    uri
                )
            )
        }
    }

    companion object {
        fun create(context: Context, parent: ViewGroup): InstagramPostViewHolder {
            return InstagramPostViewHolder(
                context,
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.feed_instagram_post_item, parent, false)
            )
        }
    }
}