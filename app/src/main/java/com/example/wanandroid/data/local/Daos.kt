package com.example.wanandroid.data.local

import androidx.room.*
import io.reactivex.Observable

interface GenericDao<T> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: T)

    @Update
    fun update(entity: T)

    @Delete
    fun delete(entity: T)
}

@Dao
interface SearchHistoryDao : GenericDao<SearchHistory> {
    @Query("SELECT * FROM search_history WHERE keyword = :keyword")
    fun queryByKeyword(keyword: String): SearchHistory?

    @Query("SELECT * FROM search_history ORDER BY timestamp DESC")
    fun queryAll(): Observable<MutableList<SearchHistory>>

    fun insertIfNotExists(entity: SearchHistory) {
        val existEntity = queryByKeyword(entity.keyword)
        if (existEntity != null) delete(existEntity)
        insert(entity)
    }
}

@Dao
interface BrowsingHistoryDao : GenericDao<BrowsingHistory> {
    @Query("SELECT * FROM browsing_history WHERE title = :title")
    fun queryByTitle(title: String): BrowsingHistory?

    @Query("SELECT * FROM browsing_history ORDER BY timestamp DESC")
    fun queryAll(): Observable<MutableList<BrowsingHistory>>

    fun insertIfNotExists(entity: BrowsingHistory) {
        val existEntity = queryByTitle(entity.title)
        if (existEntity != null) delete(existEntity)
        insert(entity)
    }
}