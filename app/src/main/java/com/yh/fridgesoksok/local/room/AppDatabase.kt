package com.yh.fridgesoksok.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.yh.fridgesoksok.local.model.FoodLocal
import com.yh.fridgesoksok.local.room.dao.FoodDao

@Database(
    entities = [FoodLocal::class],
    version = 1
)

abstract class AppDatabase : RoomDatabase() {

    abstract fun foodDao(): FoodDao

}