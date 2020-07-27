package com.example.wanandroid.account

import com.example.wanandroid.TestData
import com.example.wanandroid.data.Result
import com.example.wanandroid.data.UserRepository
import io.reactivex.Observable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class SignUpViewModelTest {
    private lateinit var viewModel: SignUpViewModel
    @Mock private lateinit var userRepository: UserRepository

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        viewModel = SignUpViewModel(userRepository)
    }

    @Test
    fun emptyFields_submitDisabled() {
        val submitEnabled = viewModel.submitEnabled.test()

        viewModel.onUsernameChanged(" ")
        viewModel.onPasswordChanged(" ")
        viewModel.onPasswordRepeated(" ")

        submitEnabled.assertValues(false, false)
    }

    @Test
    fun passwordInconsistent_submitDisabled() {
        val submitEnabled = viewModel.submitEnabled.test()

        viewModel.onUsernameChanged("foo")
        viewModel.onPasswordChanged("bar")
        viewModel.onPasswordRepeated("foobar")

        submitEnabled.assertValues(false, false)
    }

    @Test
    fun nonEmptyFields_submitEnabled() {
        val submitEnabled = viewModel.submitEnabled.test()

        viewModel.onUsernameChanged("foo")
        viewModel.onPasswordChanged("bar")
        viewModel.onPasswordRepeated("bar")

        submitEnabled.assertValues(false, true)
    }

    @Test
    fun submit_gotErrorResult() {
        val userData = viewModel.userData.test()
        `when`(userRepository.signUp(anyString(), anyString(), anyString()))
            .thenReturn(Observable.error(Throwable("error")))

        viewModel.onUsernameChanged("foo")
        viewModel.onPasswordChanged("bar")
        viewModel.onPasswordRepeated("bar")
        viewModel.onSubmitButtonClicked()

        userData.assertValue(Result.Error("error"))
    }

    @Test
    fun submit_gotFailureResult() {
        val userData = viewModel.userData.test()
        `when`(userRepository.signUp(anyString(), anyString(), anyString()))
            .thenReturn(Observable.just(Result.Failure("账号已存在")))

        viewModel.onUsernameChanged("foo")
        viewModel.onPasswordChanged("bar")
        viewModel.onPasswordRepeated("bar")
        viewModel.onSubmitButtonClicked()

        userData.assertValue(Result.Failure("账号已存在"))
    }

    @Test
    fun submit_gotSuccessResult() {
        val userData = viewModel.userData.test()
        `when`(userRepository.signUp(anyString(), anyString(), anyString()))
            .thenReturn(Observable.just(Result.Success(TestData.userData)))

        viewModel.onUsernameChanged("foo")
        viewModel.onPasswordChanged("bar")
        viewModel.onPasswordRepeated("bar")
        viewModel.onSubmitButtonClicked()

        userData.assertValue(Result.Success(TestData.userData))
    }

    companion object {
        @BeforeClass
        @JvmStatic
        fun setupSchedulers() {
            RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
            RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
        }
    }
}