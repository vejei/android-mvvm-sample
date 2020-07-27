package com.example.wanandroid.todo

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.wanandroid.common.StatusFragment
import com.example.wanandroid.data.Result
import com.example.wanandroid.databinding.FragmentTodoListBinding
import com.example.wanandroid.utils.errorMessage
import com.example.wanandroid.utils.shortMessage
import com.example.wanandroid.utils.viewModelProvider
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import timber.log.Timber
import javax.inject.Inject

class TodoListFragment : StatusFragment() {
    private lateinit var binding: FragmentTodoListBinding
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: TodoViewModel
    @Inject lateinit var disposable: CompositeDisposable
    private lateinit var adapter: TodoAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = viewModelProvider(viewModelFactory)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTodoListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRootView(binding.root)
        setLoadingView(binding.layoutLoading.root)
        setEmptyView(binding.layoutEmpty.root)
        setRetryView(binding.layoutRetry.root)
        setSuccessView(binding.swipeRefreshLayout)

        adapter = TodoAdapter(viewModel::onScrollEnd)
        with(binding) {
            swipeRefreshLayout.setOnRefreshListener {
                viewModel.onTodoRefresh()
            }
            viewModel.isTodoRefreshing.subscribe {
                swipeRefreshLayout.isRefreshing = it
            }.addTo(disposable)

            recyclerView.adapter = adapter
            viewModel.todoList.subscribe {
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
            }.addTo(disposable)

            viewModel.refreshDataEvent.subscribe { adapter.reset() }.addTo(disposable)
        }

        viewModel.fetchTodo()
    }

    override fun onRetry() {
        super.onRetry()
        if (!binding.layoutRetry.buttonRetry.hasOnClickListeners()) {
            binding.layoutRetry.buttonRetry.setOnClickListener {
                viewModel.fetchTodo()
            }
        }
    }

    override fun setupAdapterStatusView(result: Result<*>) {
        adapter.setStatus(result)
    }

    override fun onDestroy() {
        adapter.dispose()
        disposable.dispose()
        super.onDestroy()
    }

    companion object {
        private const val KEY_STATUS = "status"

        @JvmStatic
        fun newInstance(status: Int) = TodoListFragment().apply {
            arguments = Bundle().apply {
                putInt(KEY_STATUS, status)
            }
        }
    }
}