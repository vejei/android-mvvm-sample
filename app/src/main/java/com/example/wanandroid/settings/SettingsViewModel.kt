package com.example.wanandroid.settings

import androidx.lifecycle.ViewModel
import com.example.wanandroid.data.HistoryRepository
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val historyRepository: HistoryRepository
) : ViewModel() {
    private val disposables = CompositeDisposable()
    private val historiesClearEvent = PublishSubject.create<Unit>()

    init {
        historiesClearEvent.switchMapCompletable { historyRepository.deleteHistories() }
            .subscribe().addTo(disposables)
    }

    fun onHistoriesClear(): Unit = historiesClearEvent.onNext(Unit)

    override fun onCleared() {
        disposables.dispose()
        super.onCleared()
    }
}