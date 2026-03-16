package com.example.mohammedehsanullahshareef_hw1

import com.google.gson.annotations.SerializedName

// ── Items ────────────────────────────────────────────────────────

data class ApiItem(
    val id: String,
    val filename: String?,
    val imageUrl: String,
    val category: String,
    val subcategory: String?,
    val primaryColor: String,
    val secondaryColor: String?,
    val pattern: String?,
    val material: String?,
    val style: String?,
    val season: List<String>?,
    val occasionTags: List<String>?,
    val description: String?,
    val fit: String?,
    val dateAdded: String?
)

data class ItemsResponse(
    val success: Boolean,
    val items: List<ApiItem>
)

data class ItemResponse(
    val success: Boolean,
    val item: ApiItem?
)

data class DeleteResponse(
    val success: Boolean,
    val message: String?
)

// ── Search / Outfit ──────────────────────────────────────────────

data class SearchRequest(
    val query: String
)

data class OutfitItem(
    val id: String,
    val imageUrl: String,
    val category: String,
    val subcategory: String?,
    val primaryColor: String,
    val role: String?
)

data class Outfit(
    val name: String?,
    val reasoning: String?,
    val items: List<OutfitItem>
)

data class SearchResponse(
    val success: Boolean,
    val query: String?,
    val outfit: Outfit?,
    val message: String?
)
