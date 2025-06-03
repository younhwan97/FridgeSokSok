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

    @Query("SELECT DISTINCT * FROM FOOD WHERE REPLACE(itemName, ' ', '') LIKE '%' || REPLACE(:query, ' ', '') || '%' LIMIT 10")
    suspend fun searchFoodByKeyword(query: String): List<FoodLocal>

    @Query("SELECT count(*) FROM FOOD")
    suspend fun getCount(): Int

    @Query("DELETE FROM FOOD")
    suspend fun deleteFoods(): Int

}