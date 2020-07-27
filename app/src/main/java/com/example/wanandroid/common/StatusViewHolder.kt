package com.example.wanandroid.common

import android.transition.TransitionManager
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.wanandroid.data.Result
import com.example.wanandroid.databinding.LayoutAdapterStatusBinding
import timber.log.Timber

class StatusViewHolder(
    val binding: LayoutAdapterStatusBinding, val retryListener: () -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun setStatus(result: Result<*>) {
        TransitionManager.beginDelayedTransition(binding.root)
        when (result) {
            is Result.Loading -> {
                Timber.d("status view holder loading")
                binding.layoutLoading.root.visibility = View.VISIBLE
                binding.layoutDataEmpty.root.visibility = View.GONE
                binding.layoutAdapterRetry.root.visibility = View.GONE
            }
            is Result.Empty -> {
                Timber.d("status view holder empty")
                binding.layoutDataEmpty.root.visibility = View.VISIBLE
                binding.layoutLoading.root.visibility = View.GONE
                binding.layoutAdapterRetry.root.visibility = View.GONE
            }
            is Result.Error, is Result.Failure -> {
                Timber.d("status view holder error (or failure)")
                binding.layoutAdapterRetry.root.visibility = View.VISIBLE
                binding.layoutDataEmpty.root.visibility = View.GONE
                binding.layoutLoading.root.visibility = View.GONE

                binding.layoutAdapterRetry.buttonRetry.setOnClickListener(null)
                binding.layoutAdapterRetry.buttonRetry.setOnClickListener { retryListener() }
            }
        }
    }
}