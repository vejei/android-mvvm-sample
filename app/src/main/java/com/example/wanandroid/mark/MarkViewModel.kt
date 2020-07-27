package com.example.wanandroid.mark

import androidx.lifecycle.ViewModel
import com.example.wanandroid.data.*
import com.example.wanandroid.di.ActivityScoped
import com.example.wanandroid.utils.Transformers.handleError
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import javax.inject.Inject

@ActivityScoped
class MarkViewModel @Inject constructor(private val articleRepository: ArticleRepository) : ViewModel() {
    private var authenticateCookie: String? = null

    private val articleFetchEvent = PublishSubject.create<Unit>()
    private var articlePage = 0
    val isArticlesRefreshing = PublishSubject.create<Boolean>()
    var articles: Observable<Result<MutableList<Article>>>

    private val articleUnmarkEvent = PublishSubject.create<Article>()
    var unmarkResult: Observable<Result<Unit>>

    init {
        articles = articleFetchEvent.switchMap {
            articleRepository.fetchMarkedArticles(authenticateCookie ?: "", articlePage)
                .compose(handleError()).startWith(Result.Loading())
        }.doOnNext {
            if (it is Result.Success) {
                articlePage++
            }
            if (it !is Result.Loading) {
                isArticlesRefreshing.onNext(false)
            }
        }

        unmarkResult = articleUnmarkEvent.flatMap {
            articleRepository.unmarkArticle(authenticateCookie ?: "", it.id, -1)
                .compose(handleError()).startWith(Result.Loading())
        }
    }

    fun onAccountRead(username: String, password: String) {
        authenticateCookie = "loginUserName=$username; loginUserPassword=$password"
    }

    fun fetchArticles() {
        articlePage = 0
        articleFetchEvent.onNext(Unit)
    }

    fun onArticlesRefresh() {
        isArticlesRefreshing.onNext(true)
        fetchArticles()
    }

    fun onArticlesScrollEnd() {
        articleFetchEvent.onNext(Unit)
    }

    fun onArticleUnmark(article: Article) {
        articleUnmarkEvent.onNext(article)
    }
}