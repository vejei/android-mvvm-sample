package com.example.wanandroid.main.home

import com.example.wanandroid.TestData
import com.example.wanandroid.data.ArticleRepository
import com.example.wanandroid.data.BannerRepository
import com.example.wanandroid.data.Result
import com.google.common.truth.Truth.assertThat
import io.reactivex.Observable
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class HomeViewModelTest {
    @Mock private lateinit var articleRepository: ArticleRepository
    @Mock private lateinit var bannerRepository: BannerRepository
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        viewModel = HomeViewModel(articleRepository, bannerRepository)
    }

    @Test
    fun articleFetchStart_pageAtInitialValue() {
        // 文章开始加载，页码应该为初始值
        // Given - Preparation
        viewModel.articles.test()
        val argumentCaptor = ArgumentCaptor.forClass(Int::class.java)
        `when`(articleRepository.fetchHomeArticles(anyInt())).thenReturn(Observable.empty())

        // When - Execution
        viewModel.fetchStart()

        // Then - Verification
        verify(articleRepository).fetchHomeArticles(argumentCaptor.capture())
        assertThat(argumentCaptor.value).isEqualTo(0)
    }

    @Test
    fun swipeRefreshStart_pageReset() {
        // 下拉刷新开始时，页码应该重置为初始值
        // Given
        viewModel.articles.test()
        val argumentCaptor = ArgumentCaptor.forClass(Int::class.java)
        `when`(articleRepository.fetchHomeArticles(anyInt())).thenReturn(Observable.empty())

        // When
        viewModel.fetchStart()
        viewModel.onScrollEnd()
        viewModel.onSwipeRefresh()

        // Then
        verify(articleRepository, times(3))
            .fetchHomeArticles(argumentCaptor.capture())
        assertThat(argumentCaptor.allValues.last()).isEqualTo(0)
    }

    @Test
    fun articleFetchSuccess_pageIncrease() {
        viewModel.articles.test()
        val argumentCaptor = ArgumentCaptor.forClass(Int::class.java)
        `when`(articleRepository.fetchHomeArticles(anyInt()))
            .thenReturn(Observable.just(Result.Success(TestData.articleData)))

        viewModel.fetchStart()
        viewModel.onScrollEnd()

        verify(articleRepository, times(2))
            .fetchHomeArticles(argumentCaptor.capture())
        assertThat(argumentCaptor.allValues.last()).isEqualTo(1)
    }

    @Test
    fun articleFetchError_pageNotIncrease() {
        // 文章加载出错，页码应该回退到上一页，即出错时再重试的话两次使用的页码应该是一样的
        // Given
        viewModel.articles.test()
        val argumentCaptor = ArgumentCaptor.forClass(Int::class.java)
        `when`(articleRepository.fetchHomeArticles(anyInt()))
            .thenReturn(Observable.error(Throwable("something wrong")))

        // When
        viewModel.fetchStart()
        viewModel.onScrollEnd()

        // Then
        verify(articleRepository, times(2))
            .fetchHomeArticles(argumentCaptor.capture())
        val pages = argumentCaptor.allValues
        assertThat(pages[0]).isEqualTo(0)
        assertThat(pages[0]).isEqualTo(pages[1])
    }

    @Test
    fun fetchStartTrigger_articleFetchStart() {
        // 文章加载事件触发的时候，文章应该开始加载
        // Given
        val articleObserver = viewModel.articles.test()
        `when`(articleRepository.fetchHomeArticles(anyInt())).thenReturn(Observable.empty())

        // When
        viewModel.fetchStart()

        // Then
        articleObserver.assertValue(Result.Loading())
    }

    @Test
    fun swipeRefreshingTrigger_articleFetchStart() {
        // 直接刷新时，文章应该开始加载
        // Given
        val articleObserver = viewModel.articles.test()
        `when`(articleRepository.fetchHomeArticles(anyInt())).thenReturn(Observable.empty())

        // When
        viewModel.onSwipeRefresh()

        // Then
        articleObserver.assertValue(Result.Loading())
    }

    @Test
    fun scrollEndTrigger_articleFetchStart() {
        // 列表滚动到底部时，文章应该开始加载
        // Given
        val articleObserver = viewModel.articles.test()
        `when`(articleRepository.fetchHomeArticles(anyInt())).thenReturn(Observable.empty())

        // When
        viewModel.onScrollEnd()

        // Then
        articleObserver.assertValue(Result.Loading())
    }

    @Test
    fun articleFetchError_gotErrorResult() {
        // 加载出错应该返回 Result.Error
        // Given
        val articleObserver = viewModel.articles.test()
        `when`(articleRepository.fetchHomeArticles(anyInt()))
            .thenReturn(Observable.error(Throwable("something wrong")))

        // When
        viewModel.fetchStart()

        // Then
        articleObserver.assertValueAt(1, Result.Error("something wrong"))
    }

    @Test
    fun articleFetchFailure_gotFailureResult() {
        // 加载失败应该返回 Result.Failure
        // Given
        val articleObserver = viewModel.articles.test()
        `when`(articleRepository.fetchHomeArticles(anyInt()))
            .thenReturn(Observable.just(Result.Failure("加载失败")))

        // When
        viewModel.fetchStart()

        // Then
        articleObserver.assertValueAt(1, Result.Failure("加载失败"))
    }

    @Test
    fun articleDataEmpty_gotEmptyResult() {
        // 加载得到的是空数据应该返回 Result.Empty
        // Given
        val articleObserver = viewModel.articles.test()
        `when`(articleRepository.fetchHomeArticles(anyInt()))
            .thenReturn(Observable.just(Result.Empty()))

        // When
        viewModel.fetchStart()

        // Then
        articleObserver.assertValueAt(1, Result.Empty())
    }

    @Test
    fun articleFetchSuccess_gotSuccessResult() {
        // 加载成功应该返回 Result.Success
        // Given
        val articleObserver = viewModel.articles.test()
        `when`(articleRepository.fetchHomeArticles(anyInt()))
            .thenReturn(Observable.just(Result.Success(TestData.articleData)))

        // When
        viewModel.fetchStart()

        // Then
        articleObserver.assertValueAt(1, Result.Success(TestData.articleData))
    }

    @Test
    fun fetchStartTrigger_carouselFetchStart() {
        // 加载开始的时候，轮播图应该开始加载
        val carouselObserver = viewModel.carouselData.test()
        `when`(bannerRepository.fetchBanner()).thenReturn(Observable.just(Result.Empty()))

        viewModel.fetchStart()

        carouselObserver.assertValueCount(1)
    }

    @Test
    fun swipeRefreshing_carouselFetchStart() {
        // 滑动刷新的时候，轮播图应该开始加载
        val carouselObserver = viewModel.carouselData.test()
        `when`(bannerRepository.fetchBanner()).thenReturn(Observable.just(Result.Empty()))

        viewModel.onSwipeRefresh()

        carouselObserver.assertValueCount(1)
    }

    @Test
    fun carouselFetchError_gotErrorResult() {
        // 轮播图加载出错时应该返回 Result.Error
        val carouselObserver = viewModel.carouselData.test()
        `when`(bannerRepository.fetchBanner()).thenReturn(Observable.error(Throwable("something wrong")))

        viewModel.fetchStart()

        carouselObserver.assertValue(Result.Error("something wrong"))
    }

    @Test
    fun carouselFetchFailure_gotFailureResult() {
        // 轮播图加载失败时应该返回 Result.Failure
        val carouselObserver = viewModel.carouselData.test()
        `when`(bannerRepository.fetchBanner()).thenReturn(Observable.just(Result.Failure("加载失败")))

        viewModel.fetchStart()

        carouselObserver.assertValue(Result.Failure("加载失败"))
    }

    @Test
    fun carouselDataEmpty_gotEmptyResult() {
        // 轮播图请求返回的数据为空时应该返回 Result.Empty
        val carouselObserver = viewModel.carouselData.test()
        `when`(bannerRepository.fetchBanner()).thenReturn(Observable.just(Result.Empty()))

        viewModel.fetchStart()

        carouselObserver.assertValue(Result.Empty())
    }

    @Test
    fun carouselFetchSuccess_gotSuccessResult() {
        // 轮播图加载成功应该返回 Result.Success
        val carouselObserver = viewModel.carouselData.test()
        `when`(bannerRepository.fetchBanner()).thenReturn(Observable.just(Result.Success(TestData.carouselData)))

        viewModel.fetchStart()

        carouselObserver.assertValue(Result.Success(TestData.carouselData))
    }
}