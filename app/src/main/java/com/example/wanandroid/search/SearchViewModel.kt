package com.example.wanandroid.search

import androidx.lifecycle.ViewModel
import com.example.wanandroid.data.*
import com.example.wanandroid.data.local.SearchHistory
import com.example.wanandroid.di.ActivityScoped
import com.example.wanandroid.utils.Transformers.handleError
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import javax.inject.Inject

@ActivityScoped
class SearchViewModel @Inject constructor(
    private val articleRepository: ArticleRepository,
    private val searchRepository: SearchRepository
) : ViewModel() {
    private var disposables: CompositeDisposable = CompositeDisposable()

    private val hotKeywordFetchEvent = PublishSubject.create<Unit>()
    private val historyFetchEvent = PublishSubject.create<Unit>()
    var hotKeywords: Observable<Result<MutableList<HotKeyword>>>
    var histories: Observable<Result<MutableList<SearchHistory>>>

    val suggestionItemClickEvent = PublishSubject.create<String>()
    private val historyRemoveEvent = PublishSubject.create<SearchHistory>()
    val historyRemovedEvent = PublishSubject.create<SearchHistory>()
    private var clearHistoryEvent = PublishSubject.create<Unit>()

    private val searchKeyword = PublishSubject.create<String>()
    val isKeywordEmpty: Observable<Unit>
    private val searchEvent = PublishSubject.create<Unit>()
    val searchStartEvent = PublishSubject.create<Unit>()

    var searchResult: Observable<Result<MutableList<Article>>>
    private var page = 0

    init {
        hotKeywords = hotKeywordFetchEvent.switchMap {
            searchRepository.fetchHotKeywords().compose(handleError()).startWith(Result.Loading())
        }
        histories = historyFetchEvent.switchMap {
            searchRepository.querySearchHistories().compose(handleError())
                .startWith(Result.Loading())
        }

        isKeywordEmpty = searchKeyword.switchMap {
            if (it.isBlank()) {
                Observable.just(Unit)
            } else {
                Observable.empty()
            }
        }

        historyRemoveEvent.switchMapCompletable {
            searchRepository.removeHistory(it).doOnComplete { historyRemovedEvent.onNext(it) }
        }.subscribe().addTo(disposables)

        clearHistoryEvent.switchMapCompletable { searchRepository.clearHistories() }
            .subscribe()
            .addTo(disposables)

        searchResult = searchEvent.withLatestFrom(searchKeyword, BiFunction<Unit, String, String> { _, w -> w})
            .flatMap { keyword->
                Timber.d("search keyword: $keyword")
                // 调用搜索接口
                articleRepository.searchArticles(page, keyword).compose(handleError())
                    .startWith(Result.Loading())
                    .doOnNext {
                        if (it is Result.Success) {
                            Timber.d("page before increase: $page")
                            page++
                            Timber.d("page after increase: $page")
                        }
                        if (it !is Result.Loading) {
                            Timber.d("save keyword start.")
                            searchRepository.saveHistory(keyword).subscribe().addTo(disposables)
                        }
                    }
            }
    }

    fun fetchHotKeywords() {
        hotKeywordFetchEvent.onNext(Unit)
    }

    fun fetchSearchHistory() {
        historyFetchEvent.onNext(Unit)
    }

    fun onSuggestionItemClicked(itemName: String) {
        suggestionItemClickEvent.onNext(itemName)
    }

    fun onHistoryRemoveIconClicked(history: SearchHistory) {
        historyRemoveEvent.onNext(history)
    }

    fun onHistoryClearTrigger(): Unit = clearHistoryEvent.onNext(Unit)

    fun onSearchKeywordChanged(keyword: String) {
        Timber.d("onSearchKeywordChanged(keyword: $keyword)")
        searchKeyword.onNext(keyword)
    }

    fun onSearchTrigger() {
        Timber.d("onSearchTrigger()")
        searchStartEvent.onNext(Unit)
        page = 0
        searchEvent.onNext(Unit)
    }

    fun onScrollEnd() {
        searchEvent.onNext(Unit)
    }

    override fun onCleared() {
        disposables.dispose()
        super.onCleared()
    }
}