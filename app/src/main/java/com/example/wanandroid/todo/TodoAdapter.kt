package com.example.wanandroid.todo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wanandroid.common.ExtraAdapter
import com.example.wanandroid.data.Todo
import com.example.wanandroid.databinding.ItemTodoBinding
import com.example.wanandroid.utils.Transformers.throttleClick
import com.example.wanandroid.utils.formatDate
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.rxkotlin.addTo

class TodoAdapter(retryListener: () -> Unit) : ExtraAdapter<Todo>(retryListener) {
    override fun createContentViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return TodoViewHolder(
            ItemTodoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TodoViewHolder) {
            holder.bind(getItem(position))

            holder.itemView.clicks().compose(throttleClick()).subscribe {

            }.addTo(disposables)
        }
    }
}

class TodoViewHolder(private val binding: ItemTodoBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(todo: Todo) {
        with(binding) {
            textViewDate.text = todo.date.formatDate()
            textViewName.text = todo.title
            textViewDescription.text = todo.content
        }
    }
}