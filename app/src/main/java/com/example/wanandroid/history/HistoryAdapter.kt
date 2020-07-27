package com.example.wanandroid.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import com.example.wanandroid.R
import com.example.wanandroid.common.ListAdapter
import com.example.wanandroid.data.local.BrowsingHistory
import com.example.wanandroid.databinding.ItemHistoryBinding
import com.example.wanandroid.utils.escapeHtml
import com.example.wanandroid.utils.formatDate

class HistoryAdapter(
    private val historyClickListener: HistoryClickListener
) : ListAdapter<BrowsingHistory, HistoryViewHolder>() {
    private lateinit var viewHolder: HistoryViewHolder
    private var actionModeEnabled = false
    private val selectedItems = mutableListOf<BrowsingHistory>()

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        viewHolder = HistoryViewHolder(
            ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            historyClickListener, this
        )
        return viewHolder
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun enabledActionMode() {
        actionModeEnabled = true
    }

    fun disableActionMode() {
        actionModeEnabled = false
        selectedItems.clear()
        notifyDataSetChanged()
    }

    fun isActionModeEnabled(): Boolean {
        return actionModeEnabled
    }

    fun isSelected(history: BrowsingHistory): Boolean {
        return selectedItems.contains(history)
    }

    fun getSelectedItems(): MutableList<BrowsingHistory> {
        return selectedItems
    }

    fun selectItem(position: Int) {
        if (position >= itemCount) return
        val item = getItem(position)
        if (selectedItems.contains(item)) selectedItems.remove(item)
        selectedItems.add(item)
    }

    fun deselectItem(position: Int) {
        if (position >= itemCount) return
        val item = getItem(position)
        if (selectedItems.contains(item)) selectedItems.remove(item)
    }

    fun deleteSelected() {
        selectedItems.forEach {
            removeItem(it)
        }
        notifyDataSetChanged()
    }

    fun selectAll() {
        getItems().forEachIndexed { index, _ ->
            selectItem(index)
        }
        notifyDataSetChanged()
    }

    fun deselectAll() {
        selectedItems.clear()
        notifyDataSetChanged()
    }
}

class HistoryViewHolder(
    private val binding: ItemHistoryBinding,
    private val historyClickListener: HistoryClickListener,
    private val adapter: HistoryAdapter
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(history: BrowsingHistory, position: Int) {
        with(binding) {
            textViewUsername.text = history.username
            textViewPublishDate.text = history.publishDate.formatDate()
            textViewTitle.text = history.title.escapeHtml()
        }

        itemView.background = if (adapter.isActionModeEnabled()) {
            itemView.setOnClickListener(null)
            ContextCompat.getDrawable(itemView.context, R.drawable.action_mode_item_background)
        } else {
            itemView.setOnClickListener { historyClickListener.onHistoryClick(history) }
            ContextCompat.getDrawable(itemView.context, R.drawable.clickable_background)
        }

        itemView.isActivated = adapter.isSelected(history)
    }

    fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> {
        return object : ItemDetailsLookup.ItemDetails<Long>() {
            override fun getSelectionKey(): Long? {
                return itemId
            }

            override fun getPosition(): Int {
                return adapterPosition
            }
        }
    }
}

interface HistoryClickListener {
    fun onHistoryClick(history: BrowsingHistory)
}