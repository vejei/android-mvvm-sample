package com.example.wanandroid.main.category

import androidx.lifecycle.ViewModel
import com.example.wanandroid.data.*
import com.example.wanandroid.utils.Transformers.handleError
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import javax.inject.Inject

class CategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val articleRepository: ArticleRepository
) : ViewModel() {
    private val categoryFetchEvent = PublishSubject.create<Unit>()
    var categories: Observable<Result<MutableList<Category>>>
    var isCategoriesRefreshing = PublishSubject.create<Boolean>()

    private val categoryArticlesFetchEvent = PublishSubject.create<Unit>()
    var categoryArticles: Observable<Result<MutableList<Article>>>
    var isArticleRefreshing = PublishSubject.create<Boolean>()
    private var articlePage = 0
    private var categoryId = -1

    init {
        categories = categoryFetchEvent.switchMap {
            categoryRepository.fetchCategories().compose(handleError()).startWith(Result.Loading())
        }.doOnNext {
            isCategoriesRefreshing.onNext(false)
        }

        categoryArticles = categoryArticlesFetchEvent.switchMap {
            articleRepository.fetchCategoryArticles(articlePage, categoryId).compose(handleError())
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
        categoryFetchEvent.onNext(Unit)
    }

    fun onCategoriesRefresh() {
        isCategoriesRefreshing.onNext(true)
        fetchCategories()
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