package com.example.wanandroid.account

import com.example.wanandroid.TestData
import com.example.wanandroid.data.*
import io.reactivex.Observable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class SignInViewModelTest {
    @Mock private lateinit var userRepository: UserRepository
    private lateinit var viewModel: SignInViewModel

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        viewModel = SignInViewModel(userRepository)
    }

    @Test
    fun emptyFields_submitDisabled() {
        val submitEnabled = viewModel.submitEnabled.test()

        viewModel.onUsernameChanged("")
        viewModel.onPasswordChanged("")

        submitEnabled.assertValues(false, false)
    }

    @Test
    fun nonEmptyFields_submitEnabled() {
        val submitEnabled = viewModel.submitEnabled.test()

        viewModel.onUsernameChanged("foo")
        viewModel.onPasswordChanged("bar")

        submitEnabled.assertValues(false, true)
    }

    @Test
    fun submit_gotErrorResult() {
        `when`(userRepository.signIn(anyString(), anyString()))
            .thenReturn(Observable.error(Throwable("error")))
        val userData = viewModel.userData.test()

        viewModel.onUsernameChanged("foo")
        viewModel.onPasswordChanged("bar")
        viewModel.onSubmitButtonClicked()

        userData.assertValue(Result.Error<UserData>("error"))
    }

    @Test
    fun submit_gotFailureResult() {
        `when`(userRepository.signIn(anyString(), anyString()))
            .thenReturn(Observable.just(Result.Failure("failure message")))
        val userData = viewModel.userData.test()

        viewModel.onUsernameChanged("foo")
        viewModel.onPasswordChanged("bar")
        viewModel.onSubmitButtonClicked()

        userData.assertValue(Result.Failure("failure message"))
    }

    @Test
    fun submit_gotSuccessResult() {
        `when`(userRepository.signIn(anyString(), anyString()))
            .thenReturn(Observable.just(Result.Success(TestData.userData)))
        val userData = viewModel.userData.test()

        viewModel.onUsernameChanged("foo")
        viewModel.onPasswordChanged("bar")
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