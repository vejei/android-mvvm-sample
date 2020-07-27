package com.example.wanandroid.main.category

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.wanandroid.common.ListAdapter
import com.example.wanandroid.data.Category
import com.example.wanandroid.databinding.ItemCategoryBinding
import com.example.wanandroid.databinding.ItemChildCategoryBinding
import com.example.wanandroid.main.category.CategoryActivity.Companion.KEY_CATEGORY_JSON
import com.example.wanandroid.utils.Transformers.throttleClick
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.gson.Gson
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.rxkotlin.addTo

class CategoryAdapter : ListAdapter<Category, CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(
            ItemCategoryBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ))
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position))

        val context = holder.itemView.context
        holder.itemView.clicks().compose(throttleClick()).subscribe {
            val intent = Intent(context, CategoryActivity::class.java).apply {
                putExtra(KEY_CATEGORY_JSON, Gson().toJson(getItem(position)))
            }
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation((context as Activity),
                holder.binding.textViewParentName, "category_name")
            context.startActivity(intent, options.toBundle())
        }.addTo(disposables)
    }
}

class CategoryViewHolder(val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root) {
    private var childAdapter = ChildCategoryAdapter()

    fun bind(category: Category) {
        childAdapter.reset()
        childAdapter.populateFromList(category.children)

        with(binding) {
            textViewParentName.transitionName = "category_name"
            textViewParentName.text = category.name
            recyclerView.adapter = childAdapter
            recyclerView.layoutManager = FlexboxLayoutManager(itemView.context).apply {
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP
            }
        }
    }
}

class ChildCategoryAdapter : ListAdapter<Category, ChildCategoryViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildCategoryViewHolder {
        return ChildCategoryViewHolder(
            ItemChildCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ChildCategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class ChildCategoryViewHolder(val binding: ItemChildCategoryBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(category: Category) {
        binding.textViewCategoryName.text = category.name
    }
}