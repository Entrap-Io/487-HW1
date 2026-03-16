package com.example.mohammedehsanullahshareef_hw1

import androidx.room.*

@Dao
interface ClothingDao {

    @Query("SELECT * FROM clothing_items ORDER BY dateAdded DESC")
    suspend fun getAll(): List<ClothingItemEntity>

    @Query("SELECT * FROM clothing_items WHERE category = :category ORDER BY dateAdded DESC")
    suspend fun getByCategory(category: String): List<ClothingItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ClothingItemEntity)

    @Delete
    suspend fun delete(item: ClothingItemEntity)

    @Query("DELETE FROM clothing_items WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT AVG(rating) FROM clothing_items WHERE category = :category")
    suspend fun avgRatingForCategory(category: String): Float?

    @Query("SELECT COUNT(*) FROM clothing_items WHERE category = :category")
    suspend fun countForCategory(category: String): Int
}
