package com.example.mohammedehsanullahshareef_hw1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class OutfitItemAdapter(
    private var items: List<OutfitItem>,
    private val baseUrl: String
) : RecyclerView.Adapter<OutfitItemAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView  = view.findViewById(R.id.outfitItemImage)
        val role: TextView    = view.findViewById(R.id.outfitItemRole)
        val name: TextView    = view.findViewById(R.id.outfitItemName)
        val color: TextView   = view.findViewById(R.id.outfitItemColor)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_outfit_card, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        Glide.with(holder.image.context)
            .load("$baseUrl${item.imageUrl}")
            .centerCrop()
            .placeholder(android.R.drawable.ic_menu_gallery)
            .into(holder.image)
        holder.role.text  = item.role ?: item.category
        holder.name.text  = item.subcategory ?: item.category
        holder.color.text = item.primaryColor
    }

    override fun getItemCount() = items.size

    fun updateItems(newItems: List<OutfitItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}
