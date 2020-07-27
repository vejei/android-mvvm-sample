package com.example.wanandroid.main.home

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class HomeFragmentTest {

    @Before
    fun setup() {
    }

    @Test
    fun dataLoading_showLoadingLayout() {
        // 数据还在加载当中，显示加载中布局
    }

    @Test
    fun errorOccurred_showRetryLayout() {
        // 数据加载出错，显示重试布局
    }

    @Test
    fun fetchFailure_showRetryLayout() {
        // 数据加载失败，显示重试布局
    }

    @Test
    fun dataEmpty_showEmptyLayout() {
        // 获取的数据为空，显示空数据布局
    }

    @Test
    fun fetchSuccess_showDataListView() {
        // 数据加载成功，显示数据列表
    }

    @Test
    fun scrollToEndDataLoading_showLoadingLayout() {
        // 列表滚动到底部加载更多数据，加载中显示加载中布局
    }

    @Test
    fun scrollToEndErrorOccurred_showRetryLayout() {
        // 列表滚动到底部加载更多数据，加载出错显示重试布局
    }

    @Test
    fun scrollToEndDataEmpty_showEmptyLayout() {
        // 列表滚动到底部加载更多数据，数据为空显示空布局
    }
}