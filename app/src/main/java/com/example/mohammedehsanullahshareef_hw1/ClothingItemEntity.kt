package com.example.mohammedehsanullahshareef_hw1

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clothing_items")
data class ClothingItemEntity(
    @PrimaryKey val id: String,
    val imagePath: String,
    val imageUrl: String,
    val category: String,
    val subcategory: String,
    val primaryColor: String,
    val secondaryColor: String?,
    val style: String,
    val pattern: String,
    val material: String?,
    val fit: String?,
    val season: String,         // stored as comma-joined string
    val occasionTags: String,   // stored as comma-joined string
    val description: String?,
    val rating: Int,            // 1-10, user-assigned
    val dateAdded: String
)
