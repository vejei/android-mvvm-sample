package com.example.wanandroid.mark

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.wanandroid.R
import com.example.wanandroid.common.ListOnScrollListener
import com.example.wanandroid.common.StatusFragment
import com.example.wanandroid.data.Result
import com.example.wanandroid.databinding.FragmentMarkArticlesBinding
import com.example.wanandroid.utils.addDivider
import com.example.wanandroid.utils.errorMessage
import com.example.wanandroid.utils.shortMessage
import com.example.wanandroid.utils.viewModelProvider
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import timber.log.Timber
import javax.inject.Inject

class MarkArticlesFragment : StatusFragment() {
    private lateinit var binding: FragmentMarkArticlesBinding
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: MarkViewModel
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
        binding = FragmentMarkArticlesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRootView(binding.root)
        setLoadingView(binding.layoutLoading.root)
        setEmptyView(binding.layoutEmpty.root)
        setRetryView(binding.layoutRetry.root)
        setSuccessView(binding.swipeRefreshLayout)

        adapter = ArticleAdapter(viewModel::onArticlesScrollEnd)
        with(binding) {
            swipeRefreshLayout.setOnRefreshListener {
                adapter.reset()
                viewModel.onArticlesRefresh()
            }
            viewModel.isArticlesRefreshing.subscribe {
                swipeRefreshLayout.isRefreshing = it
            }.addTo(disposables)

            recyclerView.adapter = adapter
            recyclerView.addDivider()
            recyclerView.addOnScrollListener(object : ListOnScrollListener() {
                override fun onScrollEnd() {
                    viewModel.onArticlesScrollEnd()
                }
            })
            // 列表项实现滑动删除
            val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {
                override fun getMovementFlags(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ): Int {
                    val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
                    return makeMovementFlags(0, swipeFlags)
                }

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    if (viewHolder is ArticleViewHolder) {
                        val position = viewHolder.adapterPosition
                        val removedItem = adapter.onItemSwiped(viewHolder.adapterPosition)
                        if (removedItem != null) viewModel.onArticleUnmark(removedItem)

                        Timber.d("onSwiped position: $position")
                        Timber.d("onSwiped is article view holder")

                        if (position == 0) {
                            adapter.reset()
                            viewModel.onArticlesRefresh()
                        }
                    }
                }
            })
            itemTouchHelper.attachToRecyclerView(recyclerView)

            viewModel.articles.subscribe {
                Timber.d("articles subscribe")
                onStatusRequest(it, adapter.getDataSize() == 0)

                when(it) {
                    is Result.Failure -> shortMessage(it.message)
                    is Result.Error -> {
                        Timber.e(it.message)
                        errorMessage()
                    }
                    is Result.Success -> {
                        adapter.populateFromList(it.data)
                    }
                }
            }.addTo(disposables)

            viewModel.unmarkResult.subscribe {
                when(it) {
                    is Result.Failure -> shortMessage(it.message)
                    is Result.Error -> {
                        Timber.e(it.message)
                        errorMessage()
                    }
                    is Result.Success -> shortMessage(getString(R.string.article_unmarked_hint_message))
                }
            }.addTo(disposables)
        }

        viewModel.fetchArticles()
    }

    override fun onRetry() {
        super.onRetry()
        with(binding.layoutRetry) {
            if (!buttonRetry.hasOnClickListeners()) {
                buttonRetry.setOnClickListener {
                    adapter.reset()
                    viewModel.fetchArticles()
                }
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
}