package com.example.wanandroid.search

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.wanandroid.common.ListOnScrollListener
import com.example.wanandroid.common.StatusFragment
import com.example.wanandroid.data.Result
import com.example.wanandroid.databinding.FragmentSearchResultBinding
import com.example.wanandroid.main.home.ArticleAdapter
import com.example.wanandroid.utils.addDivider
import com.example.wanandroid.utils.errorMessage
import com.example.wanandroid.utils.shortMessage
import com.example.wanandroid.utils.viewModelProvider
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import timber.log.Timber
import javax.inject.Inject

class SearchResultFragment : StatusFragment() {
    private lateinit var binding: FragmentSearchResultBinding
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: SearchViewModel
    @Inject lateinit var disposables: CompositeDisposable
    private lateinit var adapter: ArticleAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = viewModelProvider(viewModelFactory)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("set up search result view.")

        setRootView(binding.root)
        setLoadingView(binding.layoutLoading.root)
        setEmptyView(binding.layoutEmpty.root)
        setRetryView(binding.layoutRetry.root)
        setSuccessView(binding.recyclerView)

        adapter = ArticleAdapter(viewModel::onScrollEnd)
        with(binding) {
            recyclerView.adapter = adapter
            recyclerView.addDivider()
            recyclerView.addOnScrollListener(object : ListOnScrollListener() {
                override fun onScrollEnd() {
                    viewModel.onScrollEnd()
                }
            })
            viewModel.searchResult.subscribe {
                Timber.d("search result subscribe.")
                onStatusRequest(it, adapter.getDataSize() == 0)

                when(it) {
                    is Result.Error -> {
                        Timber.e(it.message)
                        errorMessage()
                    }
                    is Result.Failure -> shortMessage(it.message)
                    is Result.Success -> {
                        adapter.populateFromList(it.data)
                    }
                }
            }.addTo(disposables)
        }
        viewModel.searchStartEvent.subscribe {
            adapter.reset()
        }.addTo(disposables)
    }

    override fun onRetry() {
        super.onRetry()
        if (!binding.layoutRetry.buttonRetry.hasOnClickListeners()) {
            binding.layoutRetry.buttonRetry.setOnClickListener {
                adapter.reset()
                viewModel.onSearchTrigger()
            }
        }
    }

    override fun setupAdapterStatusView(result: Result<*>) {
        adapter.setStatus(result)
    }

    override fun onDestroy() {
        adapter.dispose()
        disposables.dispose()
        super.onDestroy()
    }

    companion object {
        val TAG = SearchResultFragment::class.java.simpleName
    }
}