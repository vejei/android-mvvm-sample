package com.example.wanandroid.data

import com.example.wanandroid.data.remote.TodoService
import com.example.wanandroid.utils.Transformers.processListResponse
import com.example.wanandroid.utils.Transformers.processResponse
import io.reactivex.Observable
import java.util.*
import javax.inject.Inject

class TodoRepository @Inject constructor(private val service: TodoService) {

    fun fetchAllTodo(authenticateCookie: String, page: Int): Observable<Result<MutableList<Todo>>> {
        return service.fetchAllTodo(authenticateCookie, page).compose(processListResponse())
    }

    fun addTodo(
        authenticateCookie: String, name: String, description: String, date: Date? = null
    ): Observable<Result<Todo>> {
        return service.addTodo(authenticateCookie, name, description, date).compose(processResponse())
    }
}