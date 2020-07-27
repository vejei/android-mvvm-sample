package com.example.wanandroid.main.category

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.wanandroid.common.ListOnScrollListener
import com.example.wanandroid.common.StatusFragment
import com.example.wanandroid.data.Result
import com.example.wanandroid.databinding.FragmentCategoryPageBinding
import com.example.wanandroid.main.home.ArticleAdapter
import com.example.wanandroid.utils.addDivider
import com.example.wanandroid.utils.errorMessage
import com.example.wanandroid.utils.shortMessage
import com.example.wanandroid.utils.viewModelProvider
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import timber.log.Timber
import javax.inject.Inject

class CategoryPageFragment : StatusFragment() {
    private lateinit var binding: FragmentCategoryPageBinding
    private var cid: Int = -1

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: CategoryViewModel
    @Inject lateinit var disposables: CompositeDisposable
    private lateinit var articleAdapter: ArticleAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = viewModelProvider(viewModelFactory)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cid = arguments?.getInt(KEY_CATEGORY_ID) ?: return
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCategoryPageBinding.inflate(inflater, container, false)
        Timber.d("fragment: $this, onCreateView")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRootView(binding.root)
        setLoadingView(binding.layoutLoading.root)
        setEmptyView(binding.layoutEmpty.root)
        setRetryView(binding.layoutRetry.root)
        setSuccessView(binding.swipeRefreshLayout)

        viewModel.setCategoryId(cid)

        articleAdapter = ArticleAdapter(viewModel::onArticlesScrollEnd)
        with(binding) {
            swipeRefreshLayout.setOnRefreshListener {
                Timber.d("swipe refreshing")
                articleAdapter.reset()
                viewModel.onArticlesRefresh()
            }
            viewModel.isArticleRefreshing.subscribe {
                swipeRefreshLayout.isRefreshing = it
            }.addTo(disposables)

            recyclerView.adapter = articleAdapter
            recyclerView.addDivider()
            recyclerView.addOnScrollListener(object : ListOnScrollListener() {
                override fun onScrollEnd() {
                    Timber.d("onScrollEnd, timestamp: ${System.currentTimeMillis()}")
                    viewModel.onArticlesScrollEnd()
                }
            })
            viewModel.categoryArticles.subscribe {
                Timber.d("category articles subscribe")
                onStatusRequest(it, articleAdapter.getDataSize() == 0)

                when(it) {
                    is Result.Error -> {
                        Timber.e(it.message)
                        errorMessage()
                    }
                    is Result.Failure -> shortMessage(it.message)
                    is Result.Success -> {
                        articleAdapter.populateFromList(it.data)
                    }
                }
            }.addTo(disposables)
        }

        viewModel.fetchArticles()
    }

    override fun onRetry() {
        super.onRetry()
        if (!binding.layoutRetry.buttonRetry.hasOnClickListeners()) {
            binding.layoutRetry.buttonRetry.setOnClickListener {
                articleAdapter.reset()
                viewModel.fetchArticles()
            }
        }
    }

    override fun setupAdapterStatusView(result: Result<*>) {
        articleAdapter.setStatus(result)
    }

    override fun onDestroy() {
        articleAdapter.dispose()
        disposables.dispose()
        super.onDestroy()
    }

    companion object {
        private const val KEY_CATEGORY_ID = "category_id"

        @JvmStatic
        fun newInstance(cid: Int) =
            CategoryPageFragment().apply {
                arguments = Bundle().apply {
                    putInt(KEY_CATEGORY_ID, cid)
                }
            }
    }
}