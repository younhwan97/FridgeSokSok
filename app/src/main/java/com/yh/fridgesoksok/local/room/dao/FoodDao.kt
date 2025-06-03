package com.yh.fridgesoksok.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.yh.fridgesoksok.local.model.FoodLocal

@Dao
interface FoodDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(foods: List<FoodLocal>): List<Long>

    @Query("SELECT * FROM FOOD WHERE itemName LIKE '%' || :query || '%' LIMIT 10")
    suspend fun search(query: String): List<FoodLocal>
}