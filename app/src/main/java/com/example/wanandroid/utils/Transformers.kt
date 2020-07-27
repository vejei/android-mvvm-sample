package com.example.wanandroid.utils

import com.example.wanandroid.data.*
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import retrofit2.Response
import timber.log.Timber
import java.util.concurrent.TimeUnit

object Transformers {
    fun <T> asyncRequest(): ObservableTransformer<T, T> {
        return ObservableTransformer {
            it.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        }
    }

    fun <T> mapResponseResult(): ObservableTransformer<Response<ResponseData<T>>, Result<T>> {
        return ObservableTransformer {
            it.flatMap { response ->
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.errorCode == 0) {
                        Observable.just(Result.Success(body.data))
                    } else {
                        Observable.just(Result.Failure<T>(body?.errorMessage))
                    }
                } else {
                    val msg = response.errorBody()?.string()
                    val errorMsg = if (msg.isNullOrEmpty()) {
                        response.message()
                    } else {
                        msg
                    }
                    Observable.just(Result.Error(errorMsg))
                }
            }
        }
    }

    fun <T> mapListResponseResult(): ObservableTransformer<Response<ResponseData<ListResponse<T>>>, Result<MutableList<T>>> {
        return ObservableTransformer {
            it.compose(mapResponseResult()).flatMap { result ->
                when (result) {
                    is Result.Success -> {
                        val data = result.data?.items ?: emptyList<T>().toMutableList()
                        if (data.isEmpty()) {
                            Observable.just(Result.Empty<MutableList<T>>())
                        } else {
                            Observable.just(Result.Success(data))
                        }
                    }
                    is Result.Failure -> Observable.just(Result.Failure(result.message))
                    is Result.Error -> Observable.just(Result.Error(result.message))
                    else -> Observable.empty()
                }
            }
        }
    }

    fun <T> mapMutableListResult(): ObservableTransformer<Response<ResponseData<MutableList<T>>>, Result<MutableList<T>>> {
        return ObservableTransformer {
            it.compose(mapResponseResult()).flatMap { result ->
                when (result) {
                    is Result.Success -> {
                        val data = result.data
                        if (data == null || data.isEmpty()) {
                            Observable.just(Result.Empty(data))
                        } else {
                            Observable.just(Result.Success(data))
                        }
                    }
                    is Result.Failure -> Observable.just(Result.Failure(result.message))
                    is Result.Error -> Observable.just(Result.Error(result.message))
                    else -> Observable.empty()
                }
            }
        }
    }

    fun <T> processResponse(): ObservableTransformer<Response<ResponseData<T>>, Result<T>> {
        return ObservableTransformer {
            it.compose(asyncRequest()).compose(mapResponseResult())
        }
    }

    fun <T> processListResponse(): ObservableTransformer<Response<ResponseData<ListResponse<T>>>, Result<MutableList<T>>> {
        return ObservableTransformer {
            it.compose(asyncRequest()).compose(mapListResponseResult())
        }
    }

    fun <T> processMutableListResponse(): ObservableTransformer<Response<ResponseData<MutableList<T>>>, Result<MutableList<T>>> {
        return ObservableTransformer {
            it.compose(asyncRequest()).compose(mapMutableListResult())
        }
    }

    fun <S, T> takeWhen(w: Observable<T>): ObservableTransformer<S, S> {
        return ObservableTransformer {
            w.withLatestFrom(it, BiFunction<T, S, S> { _, source -> source })
        }
    }

    fun <T> throttleClick(): ObservableTransformer<T, T> {
        return ObservableTransformer {
            it.throttleFirst(1, TimeUnit.SECONDS)
        }
    }

    fun <T> handleError(): ObservableTransformer<Result<T>, Result<T>> {
        return ObservableTransformer {
            it.onErrorReturn { throwable ->
                Timber.d("error occurred, throwable message: ${throwable.message}")
                Timber.e("throwable: $throwable")
                Result.Error(throwable.message)
            }
        }
    }

    fun <T> mapRoomResult(): ObservableTransformer<MutableList<T>, Result<MutableList<T>>> {
        return ObservableTransformer {
            it.flatMap { queryResult ->
                if (queryResult.isEmpty()) {
                    Observable.just(Result.Empty<MutableList<T>>())
                } else {
                    Observable.just(Result.Success(queryResult))
                }
            }
        }
    }
}