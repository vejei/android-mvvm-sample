package com.example.wanandroid.account

import androidx.lifecycle.ViewModel
import com.example.wanandroid.data.Result
import com.example.wanandroid.data.UserData
import com.example.wanandroid.data.UserRepository
import com.example.wanandroid.testing.TestingOpen
import com.example.wanandroid.utils.Transformers.takeWhen
import io.reactivex.Observable
import io.reactivex.functions.Function3
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import javax.inject.Inject

@TestingOpen
class SignUpViewModel @Inject constructor(private val repository: UserRepository): ViewModel() {
    private val username = PublishSubject.create<String>()
    private val password = PublishSubject.create<String>()
    private val passwordRepeat = PublishSubject.create<String>()
    final val submitEnabled = BehaviorSubject.create<Boolean>()
    final val passwordConsistency = PublishSubject.create<Boolean>()
    private val submitButtonClickEvent = PublishSubject.create<Unit>()
    final var userData: Observable<Result<UserData>>

    data class FormData(val username: String, val password: String, val passwordRepeat: String)

    init {
        val formData = Observable.combineLatest(username, password, passwordRepeat,
            Function3<String, String, String, FormData> { u, p, pr -> FormData(u, p, pr) })

        // 检查表单字段是否为空，如果为空禁止提交，即 submitEnabled 为 false
        submitEnabled.onNext(false)
        val canSubmit = formData.map {
            it.username.isNotBlank()
                    && it.password.isNotBlank()
                    && it.passwordRepeat.isNotBlank()
                    && (it.password == it.passwordRepeat)
        }
        canSubmit.subscribe(submitEnabled)

        // 检查两个密码字段是否一致，不一致的情况下禁止提交
        val consistency = formData.map { it.passwordRepeat == it.password }
        consistency.subscribe(passwordConsistency)

        userData = formData.compose(takeWhen<FormData, Unit>(submitButtonClickEvent))
            .switchMap {
                repository.signUp(it.username, it.password, it.passwordRepeat)
                    .onErrorReturn { throwable ->
                        Result.Error(throwable.message ?: "unknown error")
                    }
            }
    }

    fun onUsernameChanged(username: String) {
        Timber.d("onUsernameChanged(username: $username)")
        this.username.onNext(username)
    }

    fun onPasswordChanged(password: String) {
        Timber.d("onPasswordChanged(password: $password)")
        this.password.onNext(password)
    }

    fun onPasswordRepeated(password: String) {
        Timber.d("onPasswordRepeated(password: $password)")
        this.passwordRepeat.onNext(password)
    }

    fun onSubmitButtonClicked() {
        Timber.d("onSubmitButtonClicked()")
        this.submitButtonClickEvent.onNext(Unit)
    }
}