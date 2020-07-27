package com.example.wanandroid.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [SearchHistory::class, BrowsingHistory::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun browsingHistoryDao(): BrowsingHistoryDao
}