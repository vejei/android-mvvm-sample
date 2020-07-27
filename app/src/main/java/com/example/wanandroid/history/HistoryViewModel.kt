package com.example.wanandroid.history

import androidx.lifecycle.ViewModel
import com.example.wanandroid.data.HistoryRepository
import com.example.wanandroid.data.Result
import com.example.wanandroid.data.local.BrowsingHistory
import com.example.wanandroid.utils.Transformers.handleError
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class HistoryViewModel @Inject constructor(
    private val historyRepository: HistoryRepository
) : ViewModel() {
    private val disposables = CompositeDisposable()

    private val fetchHistoriesEvent = PublishSubject.create<Unit>()
    var histories: Observable<Result<MutableList<BrowsingHistory>>>
    var isHistoriesRefreshing = PublishSubject.create<Boolean>()
    private val historiesClearEvent = PublishSubject.create<Unit>()
    private val selectedHistories = PublishSubject.create<MutableList<BrowsingHistory>>()
    private val deleteSelectedEvent = PublishSubject.create<Unit>()

    init {
        histories = fetchHistoriesEvent.switchMap {
            historyRepository.queryHistories().compose(handleError()).startWith(Result.Loading())
        }.doOnNext {
            if (it !is Result.Loading) isHistoriesRefreshing.onNext(false)
        }

        historiesClearEvent.switchMapCompletable { historyRepository.deleteHistories() }
            .subscribe().addTo(disposables)

        deleteSelectedEvent.withLatestFrom(selectedHistories,
            BiFunction<Unit, MutableList<BrowsingHistory>, MutableList<BrowsingHistory>> { _, l -> l }
        ).switchMapCompletable { historyRepository.deleteSelectedHistories(it) }
            .subscribe().addTo(disposables)
    }

    fun fetchHistories(): Unit = fetchHistoriesEvent.onNext(Unit)

    fun onHistoriesRefresh() {
        isHistoriesRefreshing.onNext(true)
        fetchHistoriesEvent.onNext(Unit)
    }

    fun onHistoriesClear(): Unit = historiesClearEvent.onNext(Unit)

    fun onSelectedHistoriesDelete(selectedHistories: MutableList<BrowsingHistory>) {
        this.selectedHistories.onNext(selectedHistories)
        deleteSelectedEvent.onNext(Unit)
    }

    override fun onCleared() {
        disposables.dispose()
        super.onCleared()
    }
}