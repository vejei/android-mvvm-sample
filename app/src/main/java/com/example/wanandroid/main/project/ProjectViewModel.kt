package com.example.wanandroid.main.project

import androidx.lifecycle.ViewModel
import com.example.wanandroid.data.*
import com.example.wanandroid.utils.Transformers.handleError
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import javax.inject.Inject

class ProjectViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val articleRepository: ArticleRepository
) : ViewModel() {
    private val categoriesFetchEvent = PublishSubject.create<Unit>()
    val categories: Observable<Result<MutableList<Category>>>

    private val categoryArticlesFetchEvent = PublishSubject.create<Unit>()
    val categoryArticles: Observable<Result<MutableList<Article>>>
    val isArticleRefreshing = PublishSubject.create<Boolean>()
    private var articlePage = 0
    private var categoryId = -1

    init {
        categories = categoriesFetchEvent.switchMap {
            categoryRepository.fetchProjectCategories().compose(handleError())
                .startWith(Result.Loading())
        }

        categoryArticles = categoryArticlesFetchEvent.switchMap {
            articleRepository.fetchProjectArticles(articlePage, categoryId).compose(handleError())
                .startWith(Result.Loading())
        }.doOnNext {
            isArticleRefreshing.onNext(false)
            if (it is Result.Success) {
                Timber.d("article page before success: $articlePage")
                articlePage++
                Timber.d("article page after success: $articlePage")
            }
        }
    }

    fun fetchCategories() {
        categoriesFetchEvent.onNext(Unit)
    }

    fun setCategoryId(cid: Int) {
        Timber.d("cid: $cid")
        categoryId = cid
    }

    fun fetchArticles() {
        Timber.d("fetchArticles")
        articlePage = 0
        categoryArticlesFetchEvent.onNext(Unit)
    }

    fun onArticlesRefresh() {
        isArticleRefreshing.onNext(true)
        fetchArticles()
    }

    fun onArticlesScrollEnd() {
        categoryArticlesFetchEvent.onNext(Unit)
    }
}