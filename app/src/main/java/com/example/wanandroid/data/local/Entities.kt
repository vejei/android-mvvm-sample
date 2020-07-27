package com.example.wanandroid.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_history")
data class SearchHistory(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var keyword: String,
    var timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "browsing_history")
data class BrowsingHistory(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var username: String,
    var publishDate: Long,
    var title: String,
    var url: String,
    var articleId: Int,
    var timestamp: Long = System.currentTimeMillis()
)