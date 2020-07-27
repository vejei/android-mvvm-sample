package com.example.wanandroid.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wanandroid.data.Article
import com.example.wanandroid.data.Result
import com.example.wanandroid.databinding.LayoutAdapterStatusBinding

abstract class ExtraAdapter<T>(private val retryListener: () -> Unit) : ListAdapter<T, RecyclerView.ViewHolder>() {
    private lateinit var statusViewHolder: StatusViewHolder

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ListViewType.CONTENT -> {
                createContentViewHolder(parent)
            }
            ListViewType.STATUS -> {
                statusViewHolder = StatusViewHolder(
                    LayoutAdapterStatusBinding.inflate(LayoutInflater.from(parent.context), parent,
                        false),
                    retryListener
                )
                statusViewHolder
            }
            else -> createContentViewHolder(parent)
        }
    }

    override fun getItemCount(): Int {
        return if (getDataSize() == 0) 0 else getDataSize() + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (getDataSize() == 0) {
            ListViewType.CONTENT
        } else {
            if (position < getDataSize()) {
                ListViewType.CONTENT
            } else {
                ListViewType.STATUS
            }
        }
    }

    fun setStatus(result: Result<*>) {
        statusViewHolder.setStatus(result)
    }

    abstract fun createContentViewHolder(parent: ViewGroup): RecyclerView.ViewHolder
}