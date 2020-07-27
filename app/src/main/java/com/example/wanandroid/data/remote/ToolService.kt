package com.example.wanandroid.data.remote

import com.example.wanandroid.data.ResponseData
import com.example.wanandroid.data.Site
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*

interface ToolService {
    @GET("lg/collect/usertools/json")
    fun fetchMarkedSites(
        @Header("Cookie") authenticateCookie: String
    ): Observable<Response<ResponseData<MutableList<Site>>>>

    @FormUrlEncoded
    @POST("lg/collect/addtool/json")
    fun addSite(
        @Header("Cookie") authenticateCookie: String,
        @Field("name") name: String,
        @Field("link") url: String
    ): Observable<Response<ResponseData<Site>>>

    @FormUrlEncoded
    @POST("lg/collect/updatetool/json")
    fun editSite(
        @Header("Cookie") authenticateCookie: String,
        @Field("id") id: Int,
        @Field("name") name: String,
        @Field("link") url: String
    ): Observable<Response<ResponseData<Site>>>

    @FormUrlEncoded
    @POST("lg/collect/deletetool/json")
    fun deleteSite(
        @Header("Cookie") authenticateCookie: String,
        @Field("id") id: Int
    ): Observable<Response<ResponseData<Unit>>>
}