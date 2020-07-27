package com.example.wanandroid.data

import com.example.wanandroid.data.remote.UserService
import com.example.wanandroid.testing.TestingOpen
import com.example.wanandroid.utils.Transformers.processResponse
import io.reactivex.Observable
import javax.inject.Inject

@TestingOpen
class UserRepository @Inject constructor(private var service: UserService) {

    fun signIn(username: String?, password: String?): Observable<Result<UserData>> {
        return service.signIn(username, password).compose(processResponse())
    }

    fun signUp(username: String?, password: String?, passwordRepeat: String?): Observable<Result<UserData>> {
        return service.signUp(username, password, passwordRepeat).compose(processResponse())
    }
}