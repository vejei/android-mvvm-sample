package com.example.wanandroid.main.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModelProvider
import com.example.wanandroid.common.StatusFragment
import com.example.wanandroid.data.Banner
import com.example.wanandroid.data.Result
import com.example.wanandroid.databinding.FragmentHomeBinding
import com.example.wanandroid.utils.addDivider
import com.example.wanandroid.utils.errorMessage
import com.example.wanandroid.utils.shortMessage
import com.example.wanandroid.utils.viewModelProvider
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class HomeFragment : StatusFragment() {
    private lateinit var binding: FragmentHomeBinding
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var viewModel: HomeViewModel
    @Inject lateinit var disposables: CompositeDisposable
    private lateinit var articleAdapter: ArticleAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = viewModelProvider(viewModelFactory)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("onViewCreated")

        setRootView(binding.root)
        setLoadingView(binding.layoutLoading.root)
        setEmptyView(binding.layoutEmpty.root)
        setRetryView(binding.layoutRetry.root)
        setSuccessView(binding.swipeRefreshLayout)

        articleAdapter = ArticleAdapter { viewModel.onScrollEnd() }
        with(binding) {
            // 配置 NestedScrollView
            nestedScrollView.setOnScrollChangeListener(
                NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, oldScrollY ->
                    val lastView = v?.getChildAt(v.childCount - 1)
                    if (lastView != null) {
                        if (scrollY >= (lastView.measuredHeight - v.measuredHeight)
                            && (scrollY > oldScrollY)) {
                            viewModel.onScrollEnd() // 加载更多
                        }
                    }
                })

            // 配置 SwipeRefreshLayout
            swipeRefreshLayout.setOnRefreshListener {
                articleAdapter.reset() // 刷新时重置 Adapter
                viewModel.onSwipeRefresh() // 触发刷新
            }
            viewModel.swipeRefreshing.subscribe {
                Timber.d("is refreshing? $it")
                swipeRefreshLayout.isRefreshing = it
            }.addTo(disposables)

            // 配置轮播图
            carouselView.adapter = PageAdapter()
            indicator.setWithViewPager2(carouselView.viewPager2, false) // 设置轮播图指示器
            carouselView.start(3, TimeUnit.SECONDS) // 启动轮播图
            // 轮播图数据
            viewModel.carouselData.subscribe {
                Timber.d("carousel subscribe")
                when(it) {
                    is Result.Success -> {
                        val carouselAdapter = carouselView.adapter as PageAdapter
                        carouselAdapter.setData(it.data ?: emptyList<Banner>().toMutableList())
                        indicator.itemCount = carouselAdapter.pageCount
                    }
                    is Result.Failure -> shortMessage(it.message)
                    is Result.Error -> {
                        Timber.e("error message: ${it.message}")
                        errorMessage()
                    }
                }
            }.addTo(disposables)

            // 配置文章列表的 RecyclerView
            recyclerView.adapter = articleAdapter
            recyclerView.addDivider()
            // 列表数据
            viewModel.articles.subscribe {
                Timber.d("article subscribe")
                onStatusRequest(it, articleAdapter.getDataSize() == 0)

                when (it) {
                    is Result.Success -> articleAdapter.populateFromList(it.data)
                    is Result.Failure -> shortMessage(it.message)
                    is Result.Error -> {
                        Timber.e(it.message)
                        errorMessage()
                    }
                }
            }.addTo(disposables)
        }

        viewModel.fetchStart()
    }

    override fun onRetry() {
        super.onRetry()
        if (!binding.layoutRetry.buttonRetry.hasOnClickListeners()) {
            binding.layoutRetry.buttonRetry.setOnClickListener {
                articleAdapter.reset()
                viewModel.fetchStart()
            }
        }
    }

    override fun setupAdapterStatusView(result: Result<*>) {
        articleAdapter.setStatus(result)
    }

    override fun onDestroy() {
        disposables.dispose()
        articleAdapter.dispose()
        super.onDestroy()
    }

    companion object {
        val TAG = HomeFragment::class.java.simpleName
    }
}