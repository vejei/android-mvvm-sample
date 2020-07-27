package com.example.wanandroid.todo

import androidx.lifecycle.ViewModel
import com.example.wanandroid.data.Result
import com.example.wanandroid.data.Todo
import com.example.wanandroid.data.TodoRepository
import com.example.wanandroid.di.ActivityScoped
import com.example.wanandroid.utils.Transformers.handleError
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import javax.inject.Inject

@ActivityScoped
class TodoViewModel @Inject constructor(private val repository: TodoRepository) : ViewModel() {
    private var authenticateCookie: String? = null

    private var fetchTodoEvent = PublishSubject.create<Unit>()
    var todoList: Observable<Result<MutableList<Todo>>>
    private var page = 0
    var isTodoRefreshing = PublishSubject.create<Boolean>()
    var refreshDataEvent = PublishSubject.create<Unit>()

    private val name = PublishSubject.create<String>()
    private val description = PublishSubject.create<String>()
    private val submitEvent = BehaviorSubject.create<Unit>()

    val submitEnabled = PublishSubject.create<Boolean>()
    var submitResult: Observable<Result<Todo>>

    init {
        todoList = fetchTodoEvent.switchMap {
            repository.fetchAllTodo(authenticateCookie ?: "", page)
                .compose(handleError()).startWith(Result.Loading())
        }.doOnNext {
            if (it is Result.Success) {
                page++
            }
            if (it !is Result.Loading) {
                isTodoRefreshing.onNext(false)
            }
        }

        // 表单数据
        val formData = Observable.combineLatest(name, description,
            BiFunction<String, String, FormData> { a, b -> FormData(a, b) })

        // 表单是否可以提交
        submitEnabled.onNext(false)
        val canSubmit = formData.map { it.name.isNotBlank() && it.description.isNotBlank() }
        canSubmit.subscribe(submitEnabled)

        submitResult = submitEvent.withLatestFrom(formData,
            BiFunction<Unit, FormData, FormData> {x, y -> FormData(y.name, y.description)})
            .switchMap {
                repository.addTodo(authenticateCookie ?: "", it.name, it.description)
                    .compose(handleError()).startWith(Result.Loading())
            }
    }

    data class FormData(val name: String, val description: String)

    fun onAccountRead(username: String, password: String) {
        authenticateCookie = "loginUserName=$username; loginUserPassword=$password"
        Timber.d("authenticate cookie: $authenticateCookie")
    }

    fun fetchTodo() {
        refreshDataEvent.onNext(Unit)
        page = 0
        fetchTodoEvent.onNext(Unit)
    }

    fun onTodoRefresh() {
        refreshDataEvent.onNext(Unit)
        isTodoRefreshing.onNext(true)
        fetchTodo()
    }

    fun onScrollEnd() {
        fetchTodoEvent.onNext(Unit)
    }

    fun onNameChanged(name: String) {
        this.name.onNext(name)
    }

    fun onDescriptionChanged(description: String) {
        this.description.onNext(description)
    }

    fun onSaveButtonClicked(): Unit = submitEvent.onNext(Unit)

    fun onFormClose() {
        name.onNext("")
        description.onNext("")
    }
}