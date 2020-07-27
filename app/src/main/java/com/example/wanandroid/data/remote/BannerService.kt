package com.example.wanandroid.data.remote

import com.example.wanandroid.data.ResponseData
import com.example.wanandroid.data.Banner
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET

interface BannerService {
    @GET("banner/json")
    fun fetchBanner(): Observable<Response<ResponseData<MutableList<Banner>>>>
}