package com.example.wanandroid.data

import com.example.wanandroid.data.remote.CategoryService
import com.example.wanandroid.utils.Transformers.processResponse
import io.reactivex.Observable
import javax.inject.Inject

class CategoryRepository @Inject constructor(private val categoryService: CategoryService) {

    fun fetchCategories(): Observable<Result<MutableList<Category>>> {
        return categoryService.fetchCategories().compose(processResponse())
    }

    fun fetchProjectCategories(): Observable<Result<MutableList<Category>>> {
        return categoryService.fetchProjectCategories().compose(processResponse())
    }
}