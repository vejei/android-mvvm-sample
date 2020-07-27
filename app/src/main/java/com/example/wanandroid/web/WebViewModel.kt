package com.example.wanandroid.web

import androidx.lifecycle.ViewModel
import com.example.wanandroid.data.ArticleRepository
import com.example.wanandroid.data.HistoryRepository
import com.example.wanandroid.data.Result
import com.example.wanandroid.data.local.BrowsingHistory
import com.example.wanandroid.utils.Transformers.handleError
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import javax.inject.Inject

class WebViewModel @Inject constructor(
    private val articleRepository: ArticleRepository,
    private val historyRepository: HistoryRepository
) : ViewModel() {
    private val disposables = CompositeDisposable()
    private var id: Int = -1
    private var title: String? = null
    private var url: String? = null
    private var authorName: String? = null
    private var publishDate: Long = 0
    private var authenticateCookie: String? = null
    private val markEvent = PublishSubject.create<Boolean>()
    var markResult: Observable<Result<Unit>>
    private val saveHistoryEvent = PublishSubject.create<Unit>()
//    var saveHistoryResult: Completable

    init {
        markResult = markEvent.switchMap {
            val result = if (it) {
                articleRepository.markInternalArticle(authenticateCookie ?: "", id)
            } else {
                articleRepository.markExternalArticle(authenticateCookie ?: "",
                    title ?: "", authorName ?: "", url ?: "")
            }
            result.compose(handleError()).startWith(Result.Loading())
        }

        saveHistoryEvent.switchMapCompletable {
            val history = BrowsingHistory(username = authorName ?: "", publishDate = publishDate,
                title = title ?: "", url = url ?: "", articleId = id)
            historyRepository.insertHistory(history)
        }.subscribe().addTo(disposables)
    }

    fun onAccountRead(username: String, password: String) {
        Timber.d("account info-> username: $username, password: $password")
        authenticateCookie = "loginUserName=$username; loginUserPassword=$password"
        Timber.d("cookie content: $authenticateCookie")
    }

    fun onArticleOpen(saveHistory: Boolean, id: Int, title: String?, url: String?, authorName: String?, publishDate: Long) {
        Timber.d("onArticleOpen, before check")
        Timber.d("title: $title, url: $url, authorName: $authorName")
        if (title == null || url == null || authorName == null) return

        Timber.d("onArticleOpen, after check")
        this.id = id
        this.title = title
        this.url = url
        this.authorName = authorName
        this.publishDate = publishDate

        if (saveHistory) {
            saveHistoryEvent.onNext(Unit)
        }
    }

    fun onMarkTrigger() {
        markEvent.onNext(isInternalArticle())
    }

    private fun isInternalArticle(): Boolean {
        return url?.startsWith("https://www.wanandroid.com", true) ?: false
                || url?.startsWith("http://www.wanandroid.com", true) ?: false
                || url?.startsWith("http://wanandroid.com", true) ?: false
                || url?.startsWith("https://wanandroid.com", true) ?: false
    }
}