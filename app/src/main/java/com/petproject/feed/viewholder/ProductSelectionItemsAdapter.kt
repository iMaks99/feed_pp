package com.petproject.feed.viewholder

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.product_selection_item_view.view.*

class ProductSelectionItemsAdapter(
    context: Context,
    var products: ArrayList<ProductSelectionItem>,
    var parentWidth: Int
) :
    RecyclerView.Adapter<ProductSelectionItemsAdapter.ViewHolder>() {

    private val inflater = LayoutInflater.from(context)

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.product_selection_item_view, parent, false))
    }

    override fun getItemCount(): Int {
        return products.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val params = holder.itemView.layoutParams as RecyclerView.LayoutParams
        params.height = RecyclerView.LayoutParams.MATCH_PARENT
        params.width = parentWidth - dpToPx(168f, holder.itemView.resources)
        holder.itemView.layoutParams = params

        Glide.with(holder.itemView).load(products[position].imageUrl)
            .into(holder.itemView.ProductSelectionItemImageView)


    }
}