package com.example.wanandroid.data

import com.example.wanandroid.data.local.SearchHistory
import com.example.wanandroid.data.local.SearchHistoryDao
import com.example.wanandroid.data.remote.SearchService
import com.example.wanandroid.utils.Transformers.asyncRequest
import com.example.wanandroid.utils.Transformers.mapRoomResult
import com.example.wanandroid.utils.Transformers.processResponse
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class SearchRepository @Inject constructor(
    private val searchService: SearchService,
    private val searchHistoryDao: SearchHistoryDao
) {
    fun fetchHotKeywords(): Observable<Result<MutableList<HotKeyword>>> {
        return searchService.fetchHotKeyword().compose(processResponse())
    }

    fun querySearchHistories(): Observable<Result<MutableList<SearchHistory>>> {
        return searchHistoryDao.queryAll().compose(asyncRequest()).compose(mapRoomResult())
    }

    fun saveHistory(keyword: String): Completable {
        Timber.d("save history start.")
        return Completable.fromAction { searchHistoryDao.insertIfNotExists(SearchHistory(keyword = keyword)) }
            .subscribeOn(Schedulers.io())
    }

    fun removeHistory(history: SearchHistory): Completable {
        return Completable.fromAction { searchHistoryDao.delete(history) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun clearHistories(): Completable {
        return searchHistoryDao.queryAll().flatMapIterable { it }
            .flatMapCompletable { Completable.fromAction { searchHistoryDao.delete(it) } }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}