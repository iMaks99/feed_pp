package com.petproject.feed.viewholder

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.feed_product_selection_post_item.view.*

class ProductSelectionPostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(feed: NewsFeedForListDto?) {

        val width = (itemView.context.resources.displayMetrics.widthPixels - dpToPx(
            32f,
            itemView.resources
        ))
        val params = itemView.layoutParams as RecyclerView.LayoutParams
        params.height = (1.1 * width).toInt()
        itemView.layoutParams = params

        val productSelectionPostItem = feed!!.getNewsFeedItem() as ProductSelectionPostItem

        itemView.ProductSelectionPostItemPostTitleTextView.text =
            productSelectionPostItem.name
        itemView.ProductSelectionPostItemSecondaryTitleTextView.text =
            productSelectionPostItem.descriptor
        itemView.ProductSelectionPostItemPostContentLayout.setBackgroundColor(
            Color.parseColor(
                "#${Integer.toHexString(productSelectionPostItem.color).padStart(
                    6,
                    '0'
                )}"
            )
        )

        when (productSelectionPostItem.theme.name) {
            ProductSelectionTheme.LIGHT.name -> {
                itemView.ProductSelectionPostItemPostTitleTextView.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.white
                    )
                )
                itemView.ProductSelectionPostItemSecondaryTitleTextView.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.white
                    )
                )
            }
        }

        val adapter = ProductSelectionItemsAdapter(
            itemView.context,
            productSelectionPostItem.productSelectionItem,
            width
        )

        itemView.ProductSelectionPostItemItemsRecyclerView.adapter = adapter
        itemView.ProductSelectionPostItemItemsRecyclerView.isNestedScrollingEnabled = false
        itemView.ProductSelectionPostItemItemsRecyclerView.layoutManager =
            object : LinearLayoutManager(itemView.context, HORIZONTAL, false) {
                override fun canScrollHorizontally(): Boolean {
                    return false
                }
            }

        itemView.setOnClickListener {
            val intent = Intent(itemView.context, ProductSelectionActivity::class.java)
            intent.putExtra("productSelection", productSelectionPostItem)

            val options = ActivityOptionsCompat.makeScaleUpAnimation(
                itemView, 0, 0, itemView.width, itemView.height
            ).toBundle()

            itemView.context.startActivity(intent, options)

        }
    }

    companion object {
        fun create(parent: ViewGroup): ProductSelectionPostViewHolder {
            return ProductSelectionPostViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.feed_product_selection_post_item, parent, false)
            )
        }
    }
}