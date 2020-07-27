package com.example.wanandroid.history

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.transition.TransitionManager
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StableIdKeyProvider
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.RecyclerView
import com.example.wanandroid.R
import com.example.wanandroid.common.StatusFragment
import com.example.wanandroid.data.Result
import com.example.wanandroid.data.local.BrowsingHistory
import com.example.wanandroid.databinding.FragmentHistoryBinding
import com.example.wanandroid.settings.HistoryDisableAlertDialogFragment
import com.example.wanandroid.settings.HistoryDisableAlertDialogFragment.Companion.DISABLE_HISTORY
import com.example.wanandroid.settings.HistoryDisableAlertDialogFragment.Companion.KEY_IS_DISABLE
import com.example.wanandroid.utils.addDivider
import com.example.wanandroid.utils.errorMessage
import com.example.wanandroid.utils.viewModelProvider
import com.example.wanandroid.web.WebActivity
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import timber.log.Timber
import javax.inject.Inject

class HistoryFragment : StatusFragment() {
    private lateinit var binding: FragmentHistoryBinding
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: HistoryViewModel
    @Inject lateinit var disposables: CompositeDisposable
    @Inject lateinit var preferences: SharedPreferences
    private var adapter: HistoryAdapter? = null
    private var selectionTracker: SelectionTracker<Long>? = null
    private var actionMode: ActionMode? = null
    private lateinit var actionModeCallback: ActionMode.Callback

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = viewModelProvider(viewModelFactory)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRootView(binding.root)
        setLoadingView(binding.layoutLoading.root)
        setEmptyView(binding.layoutEmpty.root)
        setRetryView(binding.layoutRetry.root)
        setSuccessView(binding.swipeRefreshLayout)

        adapter = HistoryAdapter(object : HistoryClickListener {
            override fun onHistoryClick(history: BrowsingHistory) {
                /*startActivity(Intent(activity, WebActivity::class.java).apply {
                    putExtra(WebActivity.KEY_ARTICLE_TITLE, history.title)
                    putExtra(WebActivity.KEY_ARTICLE_URL, history.url)
                    putExtra(WebActivity.KEY_ARTICLE_AUTHOR_NAME, history.username)
                    putExtra(WebActivity.KEY_ARTICLE_ID, history.articleId)
                    putExtra(WebActivity.KEY_ARTICLE_PUB_DATE, history.publishDate)
                })*/
                WebActivity.start(requireContext(), history.articleId, history.title, history.url,
                    history.username, history.publishDate)
            }
        })
        with(binding) {
            layoutHistoriesDisable.buttonEnable.setOnClickListener {
                preferences.edit().putBoolean(getString(R.string.settings_enable_history_key), true)
                    .apply()
                onHistoryEnable()
            }

            swipeRefreshLayout.setOnRefreshListener {
                adapter?.reset()
                viewModel.onHistoriesRefresh()
            }
            viewModel.isHistoriesRefreshing.subscribe {
                swipeRefreshLayout.isRefreshing = it
            }.addTo(disposables)

            recyclerView.adapter = adapter
            recyclerView.addDivider()
            viewModel.histories.subscribe {
                onStatusRequest(it)

                when(it) {
                    is Result.Error-> {
                        Timber.e(it.message)
                        errorMessage()
                    }
                    is Result.Success -> {
                        adapter?.reset()
                        adapter?.populateFromList(it.data)
                    }
                }
            }.addTo(disposables)
        }

        selectionTracker = SelectionTracker.Builder<Long>(
            "history_list", binding.recyclerView, StableIdKeyProvider(binding.recyclerView),
            DetailsLookup(binding.recyclerView), StorageStrategy.createLongStorage()
        ).build()

        selectionTracker?.addObserver(object : SelectionTracker.SelectionObserver<Long>() {
            override fun onItemStateChanged(key: Long, selected: Boolean) {
                // 选中或者取消选中调用
                if (adapter?.getDataSize() != 0) {
                    if (selected) {
                        adapter?.selectItem(key.toInt())
                    } else {
                        adapter?.deselectItem(key.toInt())
                    }
                }

                if (actionMode == null && (selectionTracker?.hasSelection() == true)) {
                    (context as AppCompatActivity).startSupportActionMode(actionModeCallback)
                    adapter?.enabledActionMode()
                    Timber.d("action mode started.")
                }
                val selectedItemSize = selectionTracker?.selection?.size()
                if (selectedItemSize == 0) {
                    actionMode?.finish()
                } else {
                    actionMode?.title = if (selectedItemSize == 0) {
                        ""
                    } else {
                        "${getString(R.string.history_action_mode_selected)} $selectedItemSize"
                    }
                }
            }
        })

        // 设置 ActionMode 实现批量操作
        actionModeCallback = object : ActionMode.Callback {
            override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                return when(item?.itemId) {
                    R.id.menu_delete_selected -> {
                        val selectedItems = adapter?.getSelectedItems()
                        if (selectedItems != null) {
                            viewModel.onSelectedHistoriesDelete(selectedItems)
                            adapter?.deleteSelected()
                            selectionTracker?.clearSelection()
                            mode?.finish()
                        }
                        true
                    }
                    R.id.menu_select_all -> {
                        val selectedItemSize = selectionTracker?.selection?.size()
                        if (selectedItemSize == adapter?.getDataSize()) {
                            selectionTracker?.clearSelection()
                            adapter?.deselectAll()
                            mode?.finish()
                        } else {
                            val dataSize = adapter?.getDataSize() ?: 0
                            for (i in 0 until dataSize) {
                                if (selectionTracker?.isSelected(i.toLong()) == false) {
                                    selectionTracker?.select(i.toLong())
                                }
                            }
                            adapter?.selectAll()
                        }
                        true
                    }
                    else -> false
                }
            }

            override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                actionMode = mode
                mode?.menuInflater?.inflate(R.menu.history_action_mode, menu)
                return true
            }

            override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                return false
            }

            override fun onDestroyActionMode(mode: ActionMode?) {
                actionMode = null
                selectionTracker?.clearSelection()
                adapter?.disableActionMode()
            }
        }

        val historyEnabled = preferences.getBoolean(getString(R.string.settings_enable_history_key), true)
        if (historyEnabled) {
            onHistoryEnable()
        } else {
            onHistoryDisable()
        }
    }

    private fun onHistoryEnable() {
        TransitionManager.beginDelayedTransition(binding.root)
        binding.layoutHistoriesDisable.root.visibility = View.GONE

        viewModel.fetchHistories()
    }

    private fun onHistoryDisable() {
        with(binding) {
            TransitionManager.beginDelayedTransition(root)
            layoutHistoriesDisable.root.visibility = View.VISIBLE

            getSuccessView().visibility = View.GONE
            getLoadingView().visibility = View.GONE
            getRetryView().visibility = View.GONE
            getEmptyView().visibility = View.GONE
        }
    }

    override fun onRetry() {
        super.onRetry()
        with(binding.layoutRetry) {
            if (!buttonRetry.hasOnClickListeners()) {
                buttonRetry.setOnClickListener {
                    adapter?.reset()
                    viewModel.fetchHistories()
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.menu_disable_history -> {
                val alertDialog = HistoryDisableAlertDialogFragment().apply {
                    setTargetFragment(this@HistoryFragment, DISABLE_HISTORY)
                }
                alertDialog.show(requireActivity().supportFragmentManager,
                    HistoryDisableAlertDialogFragment.SHOW_TAG)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == DISABLE_HISTORY && resultCode == Activity.RESULT_OK) {
            val isDisable = data?.getBooleanExtra(KEY_IS_DISABLE, false) ?: false
            if (isDisable) onHistoryDisable()
        }
    }

    override fun onDestroy() {
        adapter?.dispose()
        disposables.dispose()
        super.onDestroy()
    }
}

class DetailsLookup(private val recyclerView: RecyclerView) : ItemDetailsLookup<Long>() {
    override fun getItemDetails(e: MotionEvent): ItemDetails<Long>? {
        val childView = recyclerView.findChildViewUnder(e.x, e.y)
        return if (childView != null) {
            val viewHolder = recyclerView.getChildViewHolder(childView) as HistoryViewHolder
            viewHolder.getItemDetails()
        } else {
            null
        }
    }
}