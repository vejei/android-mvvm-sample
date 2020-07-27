package com.example.wanandroid.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wanandroid.common.ListAdapter
import com.example.wanandroid.data.local.SearchHistory
import com.example.wanandroid.databinding.ItemSearchHistoryBinding
import com.example.wanandroid.utils.Transformers.throttleClick
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.rxkotlin.addTo

class SearchHistoryAdapter(
    private val itemClickListener: OnSuggestionItemClickListener,
    private val historyRemoveListener: OnHistoryRemoveListener
) : ListAdapter<SearchHistory, SearchHistoryViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHistoryViewHolder {
        return SearchHistoryViewHolder(
            ItemSearchHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            historyRemoveListener
        )
    }

    override fun onBindViewHolder(holder: SearchHistoryViewHolder, position: Int) {
        holder.bind(getItem(position))

        holder.itemView.clicks().compose(throttleClick()).subscribe {
            itemClickListener.onClick(getItem(position).keyword)
        }.addTo(disposables)
    }
}

class SearchHistoryViewHolder(
    val binding: ItemSearchHistoryBinding,
    private val historyRemoveListener: OnHistoryRemoveListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(history: SearchHistory) {
        with(binding) {
            textViewHistoryKeyword.text = history.keyword
            imageViewCloseIcon.setOnClickListener {
                historyRemoveListener.onRemove(history)
            }
        }
    }
}

interface OnHistoryRemoveListener {
    /*
    点击删除按钮的时候做两件事：
    首先是从数据库里面删除该条数据
    然后是从Adapter里面删除该条数据
     */
    fun onRemove(history: SearchHistory)
}