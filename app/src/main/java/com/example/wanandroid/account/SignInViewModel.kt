package com.example.wanandroid.account

import androidx.lifecycle.ViewModel
import com.example.wanandroid.data.*
import com.example.wanandroid.testing.TestingOpen
import com.example.wanandroid.utils.Transformers.handleError
import com.example.wanandroid.utils.Transformers.takeWhen
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import javax.inject.Inject

@TestingOpen
class SignInViewModel @Inject constructor(private val repository: UserRepository) : ViewModel() {
    private val username = PublishSubject.create<String>()
    private val password = PublishSubject.create<String>()
    var submitEnabled = BehaviorSubject.create<Boolean>()
    lateinit var userData: Observable<Result<UserData>>
    private final val submitEvent = PublishSubject.create<Unit>()

    init {
        val formData = Observable.combineLatest(username, password,
            BiFunction<String, String, FormData> { u, p -> FormData(u, p) })

        submitEnabled.onNext(false)
        val canSubmit = formData.map { it.username.isNotBlank() && it.password.isNotBlank() }
        canSubmit.subscribe(submitEnabled)

        userData = formData.compose(takeWhen<FormData, Unit>(submitEvent)).switchMap {
            repository.signIn(it.username, it.password).compose(handleError())
        }
    }

    data class FormData(val username: String, val password: String)

    fun onUsernameChanged(username: String) {
        Timber.d("onUsernameChanged(username: $username)")
        this.username.onNext(username)
    }

    fun onPasswordChanged(password: String) {
        Timber.d("onPasswordChanged(password: $password)")
        this.password.onNext(password)
    }

    fun onSubmitButtonClicked() {
        Timber.d("onSubmitButtonClicked()")
        submitEvent.onNext(Unit)
    }
}