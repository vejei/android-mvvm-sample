package com.example.wanandroid.main.category

import com.example.wanandroid.TestData
import com.example.wanandroid.data.ArticleRepository
import com.example.wanandroid.data.CategoryRepository
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

class CategoryViewModelTest {
    private lateinit var viewModel: CategoryViewModel
    @Mock private lateinit var categoryRepository: CategoryRepository
    @Mock private lateinit var articleRepository: ArticleRepository

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        viewModel = CategoryViewModel(categoryRepository, articleRepository)
    }

    @Test
    fun fetchCategoriesEventEmit_fetchStart() {
        // 获取分类事件发送时，开始获取分类
        val observer = viewModel.categories.test()
        `when`(categoryRepository.fetchCategories()).thenReturn(Observable.empty())

        viewModel.fetchCategories()

        observer.assertValueCount(1)
        observer.assertValue(Result.Loading())
    }

    @Test
    fun fetchCategoriesError_gotErrorResult() {
        val observer = viewModel.categories.test()
        `when`(categoryRepository.fetchCategories()).thenReturn(
            Observable.error(Throwable("something wrong")))

        viewModel.fetchCategories()

        observer.assertValueAt(1, Result.Error("something wrong"))
    }

    @Test
    fun fetchCategoriesFailure_gotFailureMessage() {
        val observer = viewModel.categories.test()
        `when`(categoryRepository.fetchCategories()).thenReturn(Observable.just(
            Result.Failure("fetch categories failure")))

        viewModel.fetchCategories()

        observer.assertValueAt(1, Result.Failure("fetch categories failure"))
    }

    @Test
    fun fetchCategoriesEmpty_gotEmptyResult() {
        val observer = viewModel.categories.test()
        `when`(categoryRepository.fetchCategories()).thenReturn(Observable.just(Result.Empty()))

        viewModel.fetchCategories()

        observer.assertValueAt(1, Result.Empty())
    }

    @Test
    fun fetchCategoriesSuccess_categoriesNotEmpty() {
        val observer = viewModel.categories.test()
        `when`(categoryRepository.fetchCategories()).thenReturn(Observable.just(
            Result.Success(TestData.categoriesData)))

        viewModel.fetchCategories()

        observer.assertValueAt(1, Result.Success(TestData.categoriesData))
    }

    @Test
    fun fetchArticleStart_pageAtInitialValue() {
        // 获取文章列表开始，页码应该是初始值（也就是0）
        viewModel.categoryArticles.test()
        `when`(articleRepository.fetchCategoryArticles(anyInt(), anyInt()))
            .thenReturn(Observable.empty())
        val argumentCaptor = ArgumentCaptor.forClass(Int::class.java)

        viewModel.fetchArticles()

        verify(articleRepository).fetchCategoryArticles(argumentCaptor.capture(), anyInt())
        assertThat(argumentCaptor.value).isEqualTo(0)
    }

    @Test
    fun fetchArticleError_pageNotIncrease() {
        // 获取文章列表出错，页码应该还是原来的值（即加载第一页时出错，加载完成时还是第一页）
        viewModel.categoryArticles.test()
        `when`(articleRepository.fetchCategoryArticles(anyInt(), anyInt())).thenReturn(
            Observable.error(Throwable("something wrong")))
        val argumentCaptor = ArgumentCaptor.forClass(Int::class.java)

        viewModel.fetchArticles()
        viewModel.onArticlesScrollEnd()

        verify(articleRepository, times(2)).fetchCategoryArticles(
            argumentCaptor.capture(), anyInt())
        val pages = argumentCaptor.allValues
        assertThat(pages[0]).isEqualTo(0)
        assertThat(pages[0]).isEqualTo(pages[1])
    }

    @Test
    fun fetchArticleSuccess_pageIncrease() {
        // 获取文章列表成功，页码应该增加
        viewModel.categoryArticles.test()
        `when`(articleRepository.fetchCategoryArticles(anyInt(), anyInt())).thenReturn(
            Observable.just(Result.Success(TestData.articleData)))
        val argumentCaptor = ArgumentCaptor.forClass(Int::class.java)

        viewModel.fetchArticles()
        viewModel.onArticlesScrollEnd()

        verify(articleRepository, times(2)).fetchCategoryArticles(
            argumentCaptor.capture(), anyInt())
        val pages = argumentCaptor.allValues
        assertThat(pages[0]).isEqualTo(0)
        assertThat(pages[1]).isEqualTo(pages[0] + 1)
    }

    @Test
    fun fetchArticleStart_categoryIdEqualSetupValue() {
        // 开始加载文章列表，加载使用的 category id 应该等于 setCategoryId 方法设置的值
        viewModel.categoryArticles.test()
        `when`(articleRepository.fetchCategoryArticles(anyInt(), anyInt())).thenReturn(Observable.empty())
        val argumentCaptor = ArgumentCaptor.forClass(Int::class.java)

        viewModel.setCategoryId(101)
        viewModel.fetchArticles()

        verify(articleRepository).fetchCategoryArticles(anyInt(), argumentCaptor.capture())
        assertThat(argumentCaptor.value).isEqualTo(101)
    }
}