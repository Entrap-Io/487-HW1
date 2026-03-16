package com.example.mohammedehsanullahshareef_hw1

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CategoryStats(
    val topAvg: Float,
    val bottomAvg: Float,
    val shoesAvg: Float,
    val outerwearAvg: Float,
    val dressAvg: Float,
    val accessoryAvg: Float,
    val topCount: Int,
    val bottomCount: Int,
    val shoesCount: Int
) : Parcelable {
    fun summaryForCategory(category: String): String {
        return when (category.lowercase()) {
            "top"       -> if (topCount > 0) "Your tops avg: ${"%.1f".format(topAvg)}/10 ($topCount items)" else "No tops yet"
            "bottom"    -> if (bottomCount > 0) "Your bottoms avg: ${"%.1f".format(bottomAvg)}/10 ($bottomCount items)" else "No bottoms yet"
            "shoes"     -> if (shoesCount > 0) "Your shoes avg: ${"%.1f".format(shoesAvg)}/10 ($shoesCount items)" else "No shoes yet"
            else        -> "Closet loaded"
        }
    }
}
