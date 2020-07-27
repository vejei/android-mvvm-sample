package com.example.wanandroid.common

import android.view.View
import android.view.ViewGroup
import androidx.transition.TransitionManager
import com.example.wanandroid.data.Result

abstract class StatusFragment : InjectableFragment() {
    private lateinit var rootView: ViewGroup
    private lateinit var loadingView: ViewGroup
    private lateinit var emptyView: ViewGroup
    private lateinit var retryView: ViewGroup
    private lateinit var successView: ViewGroup

    open fun setRootView(rootView: ViewGroup) {
        this.rootView = rootView
    }

    open fun getRootView(): ViewGroup = rootView

    open fun setLoadingView(loadingView: ViewGroup) {
        this.loadingView = loadingView
    }

    open fun getLoadingView(): ViewGroup = loadingView

    open fun setEmptyView(emptyView: ViewGroup) {
        this.emptyView = emptyView
    }

    open fun getEmptyView(): ViewGroup = emptyView

    open fun setRetryView(retryView: ViewGroup) {
        this.retryView = retryView
    }

    open fun getRetryView(): ViewGroup = retryView

    open fun setSuccessView(successView: ViewGroup) {
        this.successView = successView
    }

    open fun getSuccessView(): ViewGroup = successView

    open fun onLoading() {
        loadingView.visibility = View.VISIBLE
        retryView.visibility = View.GONE
        emptyView.visibility = View.GONE
        successView.visibility = View.GONE
    }

    open fun onEmpty() {
        emptyView.visibility = View.VISIBLE
        loadingView.visibility = View.GONE
        retryView.visibility = View.GONE
        successView.visibility = View.GONE
    }

    open fun onRetry() {
        retryView.visibility = View.VISIBLE
        loadingView.visibility = View.GONE
        emptyView.visibility = View.GONE
        successView.visibility = View.GONE
    }

    open fun onSuccess() {
        successView.visibility = View.VISIBLE
        loadingView.visibility = View.GONE
        emptyView.visibility = View.GONE
        retryView.visibility = View.GONE
    }

    open fun onStatusRequest(result: Result<*>) {
        setupStatusView(result)
    }

    open fun onStatusRequest(result: Result<*>, adapterEmpty: Boolean) {
        if (adapterEmpty) {
            setupStatusView(result)
        } else {
            setupAdapterStatusView(result)
        }
    }

    open fun setupStatusView(result: Result<*>) {
        TransitionManager.beginDelayedTransition(rootView)
        when (result) {
            is Result.Loading -> onLoading()
            is Result.Empty -> onEmpty()
            is Result.Error, is Result.Failure -> onRetry()
            is Result.Success -> onSuccess()
        }
    }

    open fun setupAdapterStatusView(result: Result<*>) {}
}