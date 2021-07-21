package com.petproject.feed.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.petproject.feed.R

class NoneViewHolder(view: View) :
    RecyclerView.ViewHolder(view) {

    fun bind() {
        itemView.visibility = View.GONE
        val params = RecyclerView.LayoutParams(0, 0)
        params.setMargins(0, 0, 0, 0)
        itemView.layoutParams = params
    }

    companion object {
        fun create(parent: ViewGroup): NoneViewHolder {
            return NoneViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.feed_podcast_item, parent, false)
            )
        }
    }
}