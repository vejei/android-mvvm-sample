package com.example.wanandroid.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wanandroid.common.ListAdapter
import com.example.wanandroid.data.HotKeyword
import com.example.wanandroid.databinding.ItemSearchHotBinding
import com.example.wanandroid.utils.Transformers.throttleClick
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.rxkotlin.addTo

class HotKeywordAdapter(
    private val itemClickListener: OnSuggestionItemClickListener
) : ListAdapter<HotKeyword, HotKeywordViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HotKeywordViewHolder {
        return HotKeywordViewHolder(
            ItemSearchHotBinding.inflate(LayoutInflater.from(parent.context), parent,
                false)
        )
    }

    override fun onBindViewHolder(holder: HotKeywordViewHolder, position: Int) {
        holder.bind(getItem(position))

        holder.itemView.clicks().compose(throttleClick()).subscribe {
            val name = getItem(position).name ?: return@subscribe
            itemClickListener.onClick(name)
        }.addTo(disposables)
    }
}

class HotKeywordViewHolder(val binding: ItemSearchHotBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(keyword: HotKeyword) {
        binding.textViewKeyword.text = keyword.name
    }
}