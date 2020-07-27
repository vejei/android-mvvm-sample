package com.example.wanandroid.main.square

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.wanandroid.common.ListOnScrollListener
import com.example.wanandroid.common.StatusFragment
import com.example.wanandroid.data.*
import com.example.wanandroid.databinding.FragmentSquareBinding
import com.example.wanandroid.utils.addDivider
import com.example.wanandroid.utils.errorMessage
import com.example.wanandroid.utils.shortMessage
import com.example.wanandroid.utils.viewModelProvider
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import timber.log.Timber
import javax.inject.Inject

class SquareFragment : StatusFragment() {
    private lateinit var binding: FragmentSquareBinding
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: SquareViewModel
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
        binding = FragmentSquareBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRootView(binding.root)
        setLoadingView(binding.layoutLoading.root)
        setEmptyView(binding.layoutEmpty.root)
        setRetryView(binding.layoutRetry.root)
        setSuccessView(binding.swipeRefreshLayout)

        articleAdapter = ArticleAdapter(viewModel::onScrollEnd)
        with(binding) {
            swipeRefreshLayout.setOnRefreshListener {
                articleAdapter.reset()
                viewModel.onSwipeRefresh()
            }
            viewModel.swipeRefreshing.subscribe {
                swipeRefreshLayout.isRefreshing = it
            }.addTo(disposables)

            recyclerView.adapter = articleAdapter
            recyclerView.addDivider()
            recyclerView.addOnScrollListener(object : ListOnScrollListener() {
                override fun onScrollEnd() {
                    Timber.d("onScrollEnd, timestamp: ${System.currentTimeMillis()}")
                    viewModel.onScrollEnd()
                }
            })
            viewModel.articles.subscribe {
                Timber.d("category articles result subscribe")
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
        articleAdapter.dispose()
        disposables.dispose()
        super.onDestroy()
    }

    companion object {
        val TAG = SquareFragment::class.java.simpleName
    }
}