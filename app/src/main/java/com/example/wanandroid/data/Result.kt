package com.example.wanandroid.data

sealed class Result<out R> {
    data class Success<out T>(val data: T?) : Result<T>()

    data class Failure<out T>(val message: String?) : Result<T>()

    data class Error<out T>(var message: String?) : Result<T>() {
        init {
            if (message == null) message = "unknown error"
        }
    }

    data class Loading<out T>(val data: T? = null) : Result<T>()

    data class Empty<out T>(val data: T? = null) : Result<T>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Failure -> "Failure[message=$message]"
            is Error -> "Error[message=$message]"
            is Loading -> "Loading"
            is Empty -> "Empty"
        }
    }
}