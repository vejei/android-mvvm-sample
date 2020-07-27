package com.example.wanandroid.history

import com.example.wanandroid.data.HistoryRepository
import com.example.wanandroid.data.Result
import io.reactivex.Observable
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class HistoryViewModelTest {
    private lateinit var viewModel: HistoryViewModel
    @Mock private lateinit var historyRepository: HistoryRepository

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        viewModel = HistoryViewModel(historyRepository)
    }

    @Test
    fun fetchEventEmit_fetchStart() {
        val observer = viewModel.histories.test()
        `when`(historyRepository.queryHistories()).thenReturn(Observable.empty())

        viewModel.fetchHistories()

        observer.assertValueCount(1)
        observer.assertValue(Result.Loading())
    }
}