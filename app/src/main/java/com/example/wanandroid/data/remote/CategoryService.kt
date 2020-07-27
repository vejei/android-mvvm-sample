package com.example.wanandroid.data.remote

import com.example.wanandroid.data.ResponseData
import com.example.wanandroid.data.Category
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET

interface CategoryService {
    @GET("tree/json")
    fun fetchCategories(): Observable<Response<ResponseData<MutableList<Category>>>>

    @GET("project/tree/json")
    fun fetchProjectCategories(): Observable<Response<ResponseData<MutableList<Category>>>>
}