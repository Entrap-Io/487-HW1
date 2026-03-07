package com.example.mohammedehsanullahshareef_hw1

data class ClothingItemRequest(
    val category: String,           // "top", "bottom", etc.
    val subcategory: String?,       // "t-shirt", "jeans", ...
    val primaryColor: String,
    val secondaryColor: String?,    // or null
    val pattern: String,
    val material: String?,
    val style: String,
    val season: List<String>,
    val occasionTags: List<String>,
    val description: String,
    val fit: String
)
