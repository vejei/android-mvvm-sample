package com.example.wanandroid.data

import com.example.wanandroid.data.remote.ArticleService
import com.example.wanandroid.utils.Transformers.processListResponse
import com.example.wanandroid.utils.Transformers.processResponse
import io.reactivex.Observable
import timber.log.Timber
import javax.inject.Inject

class ArticleRepository @Inject constructor(private val service: ArticleService) {

    fun fetchHomeArticles(page: Int): Observable<Result<MutableList<Article>>> {
        return service.fetchHomeArticles(page).compose(processListResponse())
    }

    fun fetchCategoryArticles(page: Int, cid: Int): Observable<Result<MutableList<Article>>> {
        Timber.d("fetch category articles api called, page: $page, cid: $cid")
        return service.fetchCategoryArticles(page, cid).compose(processListResponse())
    }

    fun fetchProjectArticles(page: Int, categoryId: Int): Observable<Result<MutableList<Article>>> {
        return service.fetchProjectArticles(page, categoryId).compose(processListResponse())
    }

    fun fetchSquareArticles(page: Int): Observable<Result<MutableList<Article>>> {
        return service.fetchSquareArticles(page).compose(processListResponse())
    }

    fun markInternalArticle(
        authenticateCookie: String, articleId: Int
    ): Observable<Result<Unit>> {
        Timber.d("cookie content: $authenticateCookie")
        return service.markInternalArticle(authenticateCookie, articleId).compose(processResponse())
    }

    fun markExternalArticle(
        authenticateCookie: String, title: String, authorName: String, url: String
    ): Observable<Result<Unit>> {
        Timber.d("cookie content: $authenticateCookie")
        return service.markExternalArticle(authenticateCookie, title, authorName, url)
            .compose(processResponse())
    }

    fun fetchMarkedArticles(authenticateCookie: String, page: Int): Observable<Result<MutableList<Article>>> {
        return service.fetchMarkedArticle(authenticateCookie, page).compose(processListResponse())
    }

    fun unmarkArticle(authenticateCookie: String, articleId: Int, originId: Int): Observable<Result<Unit>> {
        return service.unmarkArticle(authenticateCookie, articleId, originId)
            .compose(processResponse())
    }

    fun searchArticles(page: Int, keyword: String): Observable<Result<MutableList<Article>>> {
        return service.searchArticles(page, keyword).compose(processListResponse())
    }
}