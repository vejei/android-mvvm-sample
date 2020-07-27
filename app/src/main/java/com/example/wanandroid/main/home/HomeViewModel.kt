package com.example.wanandroid.main.home

import androidx.lifecycle.ViewModel
import com.example.wanandroid.data.Article
import com.example.wanandroid.data.ArticleRepository
import com.example.wanandroid.data.Banner
import com.example.wanandroid.data.BannerRepository
import com.example.wanandroid.data.Result
import com.example.wanandroid.utils.Transformers.handleError
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val articleRepository: ArticleRepository,
    private val bannerRepository: BannerRepository
) : ViewModel() {
    private var carouselFetchEvent = PublishSubject.create<Unit>()
    private var articleFetchEvent = PublishSubject.create<Unit>()
    private var currentPage = 0 // 文章数据的页码

    var carouselData: Observable<Result<MutableList<Banner>>> // 轮播图数据
    var articles: Observable<Result<MutableList<Article>>> // 文章数据

    private var isCarouselRefreshing = PublishSubject.create<Boolean>() // 表示轮播图数据是否在加载中
    private var isArticlesRefreshing = PublishSubject.create<Boolean>() // 表示文章数据是否在加载中
    var swipeRefreshing: Observable<Boolean> // 表示数据是否在加载中，上面两项只要有一项为 true 都表示在加载中

    init {
        carouselData = carouselFetchEvent.switchMap {
            bannerRepository.fetchBanner().compose(handleError())
        }.doOnNext {
            isCarouselRefreshing.onNext(false)
        }

        articles = articleFetchEvent.switchMap {
            Timber.d("home article page: $currentPage")
            articleRepository.fetchHomeArticles(currentPage).compose(handleError())
                .startWith(Result.Loading())
        }.doOnNext {
            isArticlesRefreshing.onNext(false)
            if (it is Result.Success) {
                Timber.d("article page before success: $currentPage")
                currentPage++
                Timber.d("article page after success: $currentPage")
            }
        }

        swipeRefreshing = Observable.combineLatest(isCarouselRefreshing, isArticlesRefreshing,
            BiFunction<Boolean, Boolean, Boolean> {c, a -> c || a})
    }

    fun fetchStart() {
        Timber.d("fetch start.")
        currentPage = 0
        carouselFetchEvent.onNext(Unit)
        articleFetchEvent.onNext(Unit)
    }

    fun onScrollEnd() {
        Timber.d("onScrollEnd")
        articleFetchEvent.onNext(Unit)
    }

    fun onSwipeRefresh() {
        Timber.d("onSwipeRefresh")
        fetchStart()
        isCarouselRefreshing.onNext(true)
        isArticlesRefreshing.onNext(true)
    }
}