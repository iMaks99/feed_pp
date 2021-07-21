package com.petproject.feed.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.Resource
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.feed_rate_app_item.view.*

class RateAppViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    fun bind(adapter: FeedListAdapter) {
        itemView.RateAppPostItemPositive.setOnClickListener {
            when (itemView.RateAppPostItemTitle.text) {
                itemView.resources.getString(R.string.rate_app_main_title) -> {
                    itemView.RateAppPostItemTitle.text =
                        itemView.resources.getString(R.string.rate_app_like_title)
                    itemView.RateAppPostItemPositive.text =
                        itemView.resources.getString(R.string.rate_app_answer_ok)
                    itemView.RateAppPostItemNegative.text =
                        itemView.resources.getString(R.string.rate_app_answer_not_now)

                    adapter.notifyItemChanged(9)
                }

                itemView.resources.getString(R.string.rate_app_like_title) -> {
                    addOneMonth()
                    adapter.currentList!!.dataSource.invalidate()
                    adapter.notifyItemRemoved(9)
                    Snackbar.make(view, "TODO(link to play store)", Snackbar.LENGTH_SHORT).show()
                }

                itemView.resources.getString(R.string.rate_app_dislike_title) -> {
                    addOneMonth()
                    adapter.currentList!!.dataSource.invalidate()
                    adapter.notifyItemRemoved(9)
                    Snackbar.make(view, "TODO(mailto wylsa.com)", Snackbar.LENGTH_SHORT).show()
                }
            }
        }

        itemView.RateAppPostItemNegative.setOnClickListener {
            when (itemView.RateAppPostItemTitle.text) {
                itemView.resources.getString(R.string.rate_app_main_title) -> {
                    itemView.RateAppPostItemTitle.text =
                        itemView.resources.getString(R.string.rate_app_dislike_title)
                    itemView.RateAppPostItemPositive.text =
                        itemView.resources.getString(R.string.rate_app_answer_ok)
                    itemView.RateAppPostItemNegative.text =
                        itemView.resources.getString(R.string.rate_app_answer_not_now)

                    adapter.notifyItemChanged(9)
                }

                else -> {
                    addOneMonth()
                    adapter.currentList!!.dataSource.invalidate()
                    adapter.notifyItemRemoved(9)
                }
            }
        }
    }

    companion object {
        fun create(parent: ViewGroup): RateAppViewHolder {
            return RateAppViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.feed_rate_app_item, parent, false)
            )
        }
    }
}