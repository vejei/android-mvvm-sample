package com.example.wanandroid.main.project

import com.example.wanandroid.TestData
import com.example.wanandroid.data.ArticleRepository
import com.example.wanandroid.data.CategoryRepository
import com.example.wanandroid.data.Result
import io.reactivex.Observable
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class ProjectViewModelTest {
    private lateinit var viewModel: ProjectViewModel
    @Mock private lateinit var categoryRepository: CategoryRepository
    @Mock private lateinit var articleRepository: ArticleRepository

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        viewModel = ProjectViewModel(categoryRepository, articleRepository)
    }

    @Test
    fun fetchCategoriesEventEmit_fetchStart() {
        // 发送获取分类事件后应该开始获取分类
        val observer = viewModel.categories.test()
        `when`(categoryRepository.fetchProjectCategories()).thenReturn(Observable.empty())

        viewModel.fetchCategories()

        observer.assertValueCount(1)
        observer.assertValue(Result.Loading())
    }

    @Test
    fun fetchCategoriesError_gotErrorResult() {
        val observer = viewModel.categories.test()
        `when`(categoryRepository.fetchProjectCategories()).thenReturn(Observable.error(
            Throwable("something wrong")))

        viewModel.fetchCategories()

        observer.assertValueCount(2)
        observer.assertValueAt(1, Result.Error("something wrong"))
    }

    @Test
    fun fetchCategoriesFailure_gotFailureMessage() {
        val observer = viewModel.categories.test()
        `when`(categoryRepository.fetchProjectCategories()).thenReturn(Observable.just(
            Result.Failure("fetch categories failure")))

        viewModel.fetchCategories()

        observer.assertValueCount(2)
        observer.assertValueAt(1, Result.Failure("fetch categories failure"))
    }

    @Test
    fun fetchCategoriesEmpty_gotEmptyResult() {
        val observer = viewModel.categories.test()
        `when`(categoryRepository.fetchProjectCategories()).thenReturn(Observable.just(Result.Empty()))

        viewModel.fetchCategories()

        observer.assertValueCount(2)
        observer.assertValueAt(1, Result.Empty())
    }

    @Test
    fun fetchCategoriesSuccess_categoriesNotEmpty() {
        val observer = viewModel.categories.test()
        `when`(categoryRepository.fetchProjectCategories()).thenReturn(Observable.just(
            Result.Success(TestData.categoriesData)))

        viewModel.fetchCategories()

        observer.assertValueCount(2)
        observer.assertValueAt(1, Result.Success(TestData.categoriesData))
    }
}