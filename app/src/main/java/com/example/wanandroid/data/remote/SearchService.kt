package com.example.wanandroid.data.remote

import com.example.wanandroid.data.ResponseData
import com.example.wanandroid.data.HotKeyword
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET

interface SearchService {
    @GET("hotkey/json")
    fun fetchHotKeyword(): Observable<Response<ResponseData<MutableList<HotKeyword>>>>
}