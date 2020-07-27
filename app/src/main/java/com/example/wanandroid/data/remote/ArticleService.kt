package com.example.wanandroid.data.remote

import com.example.wanandroid.data.Article
import com.example.wanandroid.data.ResponseData
import com.example.wanandroid.data.ListResponse
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*

interface ArticleService {
    @GET("article/list/{page}/json")
    fun fetchHomeArticles(
        @Path("page") page: Int
    ): Observable<Response<ResponseData<ListResponse<Article>>>>

    @GET("article/list/{page}/json")
    fun fetchCategoryArticles(
        @Path("page") page: Int, @Query("cid") cid: Int
    ): Observable<Response<ResponseData<ListResponse<Article>>>>

    @GET("project/list/{page}/json")
    fun fetchProjectArticles(
        @Path("page") page: Int, @Query("cid") categoryId: Int
    ): Observable<Response<ResponseData<ListResponse<Article>>>>

    @GET("user_article/list/{page}/json")
    fun fetchSquareArticles(
        @Path("page") page: Int
    ): Observable<Response<ResponseData<ListResponse<Article>>>>

    @POST("lg/collect/{aid}/json")
    fun markInternalArticle(
        @Header("Cookie") authenticateCookie: String, @Path("aid") articleId: Int
    ): Observable<Response<ResponseData<Unit>>>

    @FormUrlEncoded
    @POST("lg/collect/add/json")
    fun markExternalArticle(
        @Header("Cookie") authenticateCookie: String,
        @Field("title") title: String,
        @Field("author") author: String,
        @Field("link") url: String
    ): Observable<Response<ResponseData<Unit>>>

    @GET("lg/collect/list/{page}/json")
    fun fetchMarkedArticle(
        @Header("Cookie") authenticateCookie: String,
        @Path("page") page: Int
    ): Observable<Response<ResponseData<ListResponse<Article>>>>

    @POST("lg/uncollect/{id}/json")
    fun unmarkArticle(
        @Header("Cookie") authenticateCookie: String,
        @Path("id") articleId: Int,
        @Query("originId") originId: Int
    ): Observable<Response<ResponseData<Unit>>>

    @POST("article/query/{page}/json")
    fun searchArticles(
        @Path("page") page: Int, @Query("k") keyword: String
    ): Observable<Response<ResponseData<ListResponse<Article>>>>
}