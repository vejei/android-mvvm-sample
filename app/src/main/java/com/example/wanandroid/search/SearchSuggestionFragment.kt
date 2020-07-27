package com.example.wanandroid.search

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.wanandroid.R
import com.example.wanandroid.common.InjectableFragment
import com.example.wanandroid.data.Result
import com.example.wanandroid.data.local.SearchHistory
import com.example.wanandroid.databinding.FragmentSearchSuggestionBinding
import com.example.wanandroid.utils.Transformers.throttleClick
import com.example.wanandroid.utils.errorMessage
import com.example.wanandroid.utils.shortMessage
import com.example.wanandroid.utils.viewModelProvider
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxItemDecoration
import com.google.android.flexbox.FlexboxLayoutManager
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import timber.log.Timber
import javax.inject.Inject

class SearchSuggestionFragment : InjectableFragment() {
    private lateinit var binding: FragmentSearchSuggestionBinding
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: SearchViewModel
    @Inject lateinit var disposables: CompositeDisposable

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = viewModelProvider(viewModelFactory)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchSuggestionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            // 搜索热词列表布局
            val flexboxLayoutManager = FlexboxLayoutManager(context).apply {
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP
            }
            val itemDecoration = FlexboxItemDecoration(context).apply {
                setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.flex_divider))
            }

            recyclerViewHot.layoutManager = flexboxLayoutManager
            recyclerViewHot.adapter = HotKeywordAdapter(object : OnSuggestionItemClickListener {
                override fun onClick(itemName: String) {
                    viewModel.onSuggestionItemClicked(itemName)
                }
            })
            recyclerViewHot.addItemDecoration(itemDecoration)
            viewModel.hotKeywords.subscribe {
                // 成功拿到搜索热词
                Timber.d("hot keyword subscribe")
                setupHotKeywordLayoutVisibility(it)
                when(it) {
                    is Result.Failure -> {
                        Timber.d("request failure, failure message: ${it.message}")
                        shortMessage(it.message)
                    }
                    is Result.Error -> {
                        Timber.e(it.message)
                        errorMessage()
                    }
                    is Result.Success -> {
                        Timber.d("request success.")
                        val adapter = (recyclerViewHot.adapter as HotKeywordAdapter)
                        adapter.reset()
                        adapter.populateFromList(it.data)
                    }
                }
            }.addTo(disposables)

            // 搜索历史列表布局
            val searchHistoryAdapter = SearchHistoryAdapter(object : OnSuggestionItemClickListener {
                override fun onClick(itemName: String) {
                    viewModel.onSuggestionItemClicked(itemName)
                }
            }, object : OnHistoryRemoveListener {
                override fun onRemove(history: SearchHistory) {
                    viewModel.onHistoryRemoveIconClicked(history)
                }
            })
            recyclerViewHistory.adapter = searchHistoryAdapter
            viewModel.histories.subscribe {
                // 成功拿到搜索历史
                Timber.d("histories subscribe")
                when(it) {
                    is Result.Empty -> {
                        Timber.d("history request success, but data empty, remove views.")
                        textViewHistoryTitle.visibility = View.GONE
                        textViewClearHistory.visibility = View.GONE
                        recyclerViewHistory.visibility = View.GONE
                    }
                    is Result.Success -> {
                        Timber.d("history request success.")
                        textViewHistoryTitle.visibility = View.VISIBLE
                        textViewClearHistory.visibility = View.VISIBLE
                        recyclerViewHistory.visibility = View.VISIBLE

                        val adapter = (recyclerViewHistory.adapter as SearchHistoryAdapter)
                        adapter.reset()
                        var data = it.data ?: emptyList<SearchHistory>().toMutableList()
                        if (data.size > 10) {
                            data = data.subList(0, 10)
                        }
                        adapter.populateFromList(data)
                    }
                }
            }.addTo(disposables)

            textViewClearHistory.clicks().compose(throttleClick()).subscribe {
                // 清除搜索历史
                Timber.d("clear history trigger.")
                viewModel.onHistoryClearTrigger()
            }.addTo(disposables)

            viewModel.historyRemovedEvent.subscribe { searchHistoryAdapter.removeItem(it) }
                .addTo(disposables)
        }

        viewModel.fetchHotKeywords() // 获取搜索热词
        viewModel.fetchSearchHistory() // 获取搜索历史
    }

    private fun setupHotKeywordLayoutVisibility(result: Result<*>) {
        with(binding) {
            when (result) {
                is Result.Failure, is Result.Error, is Result.Empty -> {
                    textViewHotTitle.visibility = View.GONE
                    recyclerViewHot.visibility = View.GONE
                }
                is Result.Success -> {
                    textViewHotTitle.visibility = View.VISIBLE
                    recyclerViewHot.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onDestroy() {
        with(binding) {
            (recyclerViewHot.adapter as HotKeywordAdapter).dispose()
            (recyclerViewHistory.adapter as SearchHistoryAdapter).dispose()
        }
        disposables.dispose()
        super.onDestroy()
    }

    companion object {
        val TAG = SearchSuggestionFragment::class.java.simpleName
    }
}