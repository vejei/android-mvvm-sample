package com.example.wanandroid.search

import com.example.wanandroid.any
import com.example.wanandroid.data.ArticleRepository
import com.example.wanandroid.data.Result
import com.example.wanandroid.data.SearchRepository
import com.example.wanandroid.data.local.SearchHistory
import io.reactivex.Completable
import io.reactivex.Observable
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class SearchViewModelTest {
    private lateinit var viewModel: SearchViewModel
    @Mock private lateinit var articleRepository: ArticleRepository
    @Mock private lateinit var searchRepository: SearchRepository

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        viewModel = SearchViewModel(articleRepository, searchRepository)
    }

    @Test
    fun fetchHotKeywordTrigger_hotKeywordFetchStart() {
        val observer = viewModel.hotKeywords.test()
        `when`(searchRepository.fetchHotKeywords()).thenReturn(Observable.empty())

        viewModel.fetchHotKeywords()

        observer.assertValueCount(1)
        observer.assertValue(Result.Loading())
    }

    @Test
    fun fetchHistoryTrigger_historyFetchStart() {
        val observer = viewModel.histories.test()
        `when`(searchRepository.querySearchHistories()).thenReturn(Observable.empty())

        viewModel.fetchSearchHistory()

        observer.assertValueCount(1)
        observer.assertValue(Result.Loading())
    }

    @Test
    fun suggestionItemClicked_suggestionItemClickEventHasValue() {
        val observer = viewModel.suggestionItemClickEvent.test()

        viewModel.onSuggestionItemClicked("foo")

        observer.assertValueCount(1)
        observer.assertValue("foo")
    }

    @Test
    fun removeHistory_removedEventHasValue() {
        val observer = viewModel.historyRemovedEvent.test()
        `when`(searchRepository.removeHistory(any())).thenReturn(Completable.complete())

        viewModel.onHistoryRemoveIconClicked(SearchHistory(keyword = "foo"))

        observer.assertValueCount(1)
    }

    @Test
    fun clearHistoryTrigger_clearHistoriesCalled() {
        `when`(searchRepository.clearHistories()).thenReturn(Completable.complete())

        viewModel.onHistoryClearTrigger()

        verify(searchRepository).clearHistories()
    }

    @Test
    fun searchKeywordEmpty_isKeywordEmptyHasValue() {
        val observer = viewModel.isKeywordEmpty.test()

        viewModel.onSearchKeywordChanged("")

        observer.assertValueCount(1)
    }

    @Test
    fun searchKeywordNotEmpty_isKeywordEmptyHasNoValue() {
        val observer = viewModel.isKeywordEmpty.test()

        viewModel.onSearchKeywordChanged("foo")

        observer.assertNoValues()
    }

    @Test
    fun searchTrigger_searchStartEventHasValue() {
        val observer = viewModel.searchStartEvent.test()

        viewModel.onSearchKeywordChanged("foo")
        viewModel.onSearchTrigger()

        observer.assertValueCount(1)
    }
}