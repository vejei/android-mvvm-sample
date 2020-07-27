package com.example.wanandroid.common

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber

abstract class ListOnScrollListener : RecyclerView.OnScrollListener() {

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (dy > 0) {
            if (recyclerView.layoutManager !is LinearLayoutManager) {
                throw Throwable("LayoutManager other than LinearLayoutManager are not supported.")
            }
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val currentVisibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            Timber.d("firstVisibleItemPosition: $firstVisibleItemPosition")
            Timber.d("currentVisibleItemCount: $currentVisibleItemCount")
            Timber.d("totalItemCount: $totalItemCount")

            if (totalItemCount <= (currentVisibleItemCount + firstVisibleItemPosition)) {
                onScrollEnd()
            }
        }
    }

    abstract fun onScrollEnd()
}