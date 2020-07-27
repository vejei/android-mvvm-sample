package com.example.wanandroid.data

import com.example.wanandroid.data.local.BrowsingHistory
import com.example.wanandroid.data.local.BrowsingHistoryDao
import com.example.wanandroid.utils.Transformers.asyncRequest
import com.example.wanandroid.utils.Transformers.mapRoomResult
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class HistoryRepository @Inject constructor(private val historyDao: BrowsingHistoryDao) {

    fun insertHistory(history: BrowsingHistory): Completable {
        return Completable.fromAction { historyDao.insertIfNotExists(history) }
            .subscribeOn(Schedulers.io())
    }

    fun queryHistories(): Observable<Result<MutableList<BrowsingHistory>>> {
        return historyDao.queryAll().compose(asyncRequest()).compose(mapRoomResult())
    }

    fun deleteHistory(history: BrowsingHistory): Completable {
        return Completable.fromAction { historyDao.insertIfNotExists(history) }
    }

    fun deleteHistories(): Completable {
        return historyDao.queryAll().flatMapIterable { it }
            .flatMapCompletable { Completable.fromAction { historyDao.delete(it) } }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun deleteSelectedHistories(histories: MutableList<BrowsingHistory>): Completable {
        return Observable.fromIterable(histories)
            .flatMapCompletable { Completable.fromAction { historyDao.delete(it) } }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}