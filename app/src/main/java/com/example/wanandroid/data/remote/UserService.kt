package com.example.wanandroid.data.remote

import com.example.wanandroid.data.ResponseData
import com.example.wanandroid.data.UserData
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * 账号相关的接口
 */
interface UserService {
    @POST("user/login")
    fun signIn(
        @Query("username") username: String?,
        @Query("password") password: String?
    ): Observable<Response<ResponseData<UserData>>>

    @POST("user/register")
    fun signUp(
        @Query("username") username: String?,
        @Query("password") password: String?,
        @Query("repassword") passwordRepeat: String?
    ): Observable<Response<ResponseData<UserData>>>
}