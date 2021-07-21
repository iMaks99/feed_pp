package com.petproject.feed.viewholder

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.android.synthetic.main.instagram_media_item_view.view.*

class InstagramPostSliderAdapter(
    var context: Context,
    var relativeLayout: RelativeLayout,
    var mediaList: ArrayList<InstagramPostMediaItem>
) :
    PagerAdapter() {

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view == obj
    }

    override fun getCount(): Int {
        return mediaList.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater: LayoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view =
            inflater.inflate(R.layout.instagram_media_item_view, (container as ViewPager), false)

        Glide.with(view)
            .asBitmap()
            .load(mediaList[position].mediaUrl)
//            .into(view.InstagramPostMediaItemImageView)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    relativeLayout.layoutParams.height = (
                            (resource.height
                                    * (view.context.resources.displayMetrics.widthPixels
                                            - dpToPx(82f, view.context.resources)))
                            / resource.width)

                    view.InstagramPostMediaItemImageView.layoutParams.height = (
                            (resource.height
                                    * (view.context.resources.displayMetrics.widthPixels
                                    - dpToPx(82f, view.context.resources)))
                                    / resource.width)

                    view.InstagramPostMediaItemImageView.setImageBitmap(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })


        val viewPager: ViewPager = container
        viewPager.addView(view, 0)

        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        val viewPager: ViewPager = container as ViewPager
        val view: View = obj as View
        viewPager.removeView(view)
    }
}