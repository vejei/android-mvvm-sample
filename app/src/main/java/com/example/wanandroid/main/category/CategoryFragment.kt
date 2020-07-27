package com.example.wanandroid.main.category

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.wanandroid.common.StatusFragment
import com.example.wanandroid.data.Result
import com.example.wanandroid.databinding.FragmentCategoryBinding
import com.example.wanandroid.utils.addDivider
import com.example.wanandroid.utils.errorMessage
import com.example.wanandroid.utils.shortMessage
import com.example.wanandroid.utils.viewModelProvider
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import timber.log.Timber
import javax.inject.Inject

class CategoryFragment : StatusFragment() {
    private lateinit var binding: FragmentCategoryBinding
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: CategoryViewModel
    @Inject lateinit var disposables: CompositeDisposable
    private lateinit var adapter: CategoryAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = viewModelProvider(viewModelFactory)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRootView(binding.root)
        setLoadingView(binding.layoutLoading.root)
        setEmptyView(binding.layoutEmpty.root)
        setRetryView(binding.layoutRetry.root)
        setSuccessView(binding.swipeRefreshLayout)

        adapter = CategoryAdapter()
        with(binding) {
            swipeRefreshLayout.setOnRefreshListener {
                adapter.reset()
                viewModel.onCategoriesRefresh()
            }
            viewModel.isCategoriesRefreshing.subscribe {
                swipeRefreshLayout.isRefreshing = it
            }.addTo(disposables)

            recyclerView.adapter = adapter
            recyclerView.addDivider()
            viewModel.categories.subscribe {
                onStatusRequest(it)

                when (it) {
                    is Result.Error -> {
                        Timber.e(it.message)
                        errorMessage()
                    }
                    is Result.Failure -> shortMessage(it.message)
                    is Result.Success -> {
                        (recyclerView.adapter as CategoryAdapter).populateFromList(it.data)
                    }
                }
            }.addTo(disposables)
        }

        viewModel.fetchCategories()
    }

    override fun onRetry() {
        super.onRetry()
        if (!binding.layoutRetry.buttonRetry.hasOnClickListeners()) {
            binding.layoutRetry.buttonRetry.setOnClickListener {
                viewModel.fetchCategories()
            }
        }
    }

    override fun onDestroy() {
        adapter.dispose()
        disposables.dispose()
        super.onDestroy()
    }

    companion object {
        val TAG = CategoryFragment::class.java.simpleName
    }
}