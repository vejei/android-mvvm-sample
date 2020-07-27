package com.example.wanandroid.main.square

import androidx.lifecycle.ViewModel
import com.example.wanandroid.data.Article
import com.example.wanandroid.data.ArticleRepository
import com.example.wanandroid.data.Result
import com.example.wanandroid.utils.Transformers.handleError
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import javax.inject.Inject

class SquareViewModel @Inject constructor(
    private val articleRepository: ArticleRepository
) : ViewModel() {
    private val articleFetchEvent = PublishSubject.create<Unit>()
    val articles: Observable<Result<MutableList<Article>>>
    private var articlePage = 0

    val swipeRefreshing = PublishSubject.create<Boolean>()

    init {
        articles = articleFetchEvent.switchMap {
            articleRepository.fetchSquareArticles(articlePage).compose(handleError())
                .startWith(Result.Loading())
        }.doOnNext {
            swipeRefreshing.onNext(false)
            if (it is Result.Success) {
                Timber.d("article page before success: $articlePage")
                articlePage++
                Timber.d("article page after success: $articlePage")
            }
        }
    }

    fun fetchStart() {
        articlePage = 0
        articleFetchEvent.onNext(Unit)
    }

    fun onScrollEnd() {
        articleFetchEvent.onNext(Unit)
    }

    fun onSwipeRefresh() {
        swipeRefreshing.onNext(true)
        fetchStart()
    }
}