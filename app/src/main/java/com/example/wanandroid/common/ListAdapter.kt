package com.example.wanandroid.common

import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

abstract class ListAdapter<T, VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {
    private var data = mutableListOf<T>()
    protected var disposables = CompositeDisposable()

    override fun getItemCount(): Int = data.size

    fun getItem(position: Int): T = data[position]

    fun getItems(): MutableList<T> {
        return (mutableListOf<T>() + data).toMutableList()
    }

    fun getDataSize(): Int = data.size

    open fun populateFromList(data: MutableList<T>?) {
        if (data == null) return

        Observable.fromIterable(data).filter { it != null }
            .concatMap { Observable.just(it) }
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                this.data.add(it)
                notifyDataSetChanged()
            }.addTo(disposables)
    }

    fun addItem(position: Int, item: T) {
        data.add(position, item)
        notifyItemInserted(position)
    }

    fun removeItem(item: T) {
        val position = data.indexOf(item)
        data.remove(item)
        notifyItemRemoved(position)
    }

    fun removeItemAt(position: Int) {
        data.removeAt(position)
        notifyItemRemoved(position)
    }

    fun reset() {
        data.clear()
        notifyDataSetChanged()
    }

    fun dispose() {
        disposables.dispose()
    }
}