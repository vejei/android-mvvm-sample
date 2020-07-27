package com.example.wanandroid.data.remote

import com.example.wanandroid.data.ListResponse
import com.example.wanandroid.data.ResponseData
import com.example.wanandroid.data.Todo
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*
import java.util.*

interface TodoService {
    @GET("lg/todo/v2/list/{page}/json")
    fun fetchAllTodo(
        @Header("Cookie") authenticateCookie: String, @Path("page") page: Int
    ): Observable<Response<ResponseData<ListResponse<Todo>>>>

    @FormUrlEncoded
    @POST("lg/todo/add/json")
    fun addTodo(
        @Header("Cookie") authenticateCookie: String,
        @Field("title") name: String,
        @Field("content") description: String,
        @Field("date") date: Date? = null,
        @Field("type") type: Int? = null,
        @Field("priority") priority: Int? = null
    ): Observable<Response<ResponseData<Todo>>>
}