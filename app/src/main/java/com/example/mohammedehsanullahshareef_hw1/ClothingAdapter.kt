package com.example.mohammedehsanullahshareef_hw1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ClothingAdapter(
    private var items: List<ClothingItemEntity>,
    private val baseUrl: String,
    private val onDelete: (ClothingItemEntity) -> Unit
) : RecyclerView.Adapter<ClothingAdapter.ViewHolder>() {

    private var opacity: Float = 1f

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView     = view.findViewById(R.id.itemImage)
        val subcategory: TextView = view.findViewById(R.id.itemSubcategory)
        val color: TextView      = view.findViewById(R.id.itemColor)
        val rating: TextView     = view.findViewById(R.id.itemRating)
        val deleteBtn: Button    = view.findViewById(R.id.deleteBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_clothing_card, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        Glide.with(holder.image.context)
            .load("$baseUrl${item.imageUrl}")
            .centerCrop()
            .placeholder(android.R.drawable.ic_menu_gallery)
            .into(holder.image)

        holder.image.alpha           = opacity
        holder.subcategory.text      = item.subcategory.ifEmpty { item.category }
        holder.color.text            = item.primaryColor + (item.secondaryColor?.let { " · $it" } ?: "")
        holder.rating.text           = "⭐ ${item.rating}/10"
        holder.deleteBtn.setOnClickListener { onDelete(item) }
    }

    override fun getItemCount() = items.size

    fun updateItems(newItems: List<ClothingItemEntity>) {
        items = newItems
        notifyDataSetChanged()
    }

    fun updateOpacity(newOpacity: Float) {
        opacity = newOpacity
        notifyDataSetChanged()
    }
}
