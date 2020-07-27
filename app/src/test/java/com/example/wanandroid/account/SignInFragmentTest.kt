package com.example.wanandroid.account

import android.accounts.AccountManager
import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import com.example.wanandroid.R
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.example.wanandroid.IsProcessingMatcher.Companion.isProcessing
import com.example.wanandroid.MockActivityLifecycleCallbacks
import com.example.wanandroid.TestData
import com.example.wanandroid.data.Result
import com.example.wanandroid.data.UserRepository
import com.google.common.truth.Truth.assertThat
import io.reactivex.Observable
import org.hamcrest.Matchers.not
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.*
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadows.ShadowToast

@RunWith(RobolectricTestRunner::class)
class SignInFragmentTest {
    private lateinit var activity: AccountActivity
    @Mock private lateinit var accountManager: AccountManager
    private var fragment: SignInFragment = SignInFragment()
    private lateinit var fragmentManager: FragmentManager

    private lateinit var viewModel: SignInViewModel
    @Mock private lateinit var userRepository: UserRepository

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        viewModel = SignInViewModel(userRepository)

        activity = Robolectric.setupActivity(AccountActivity::class.java)

        fragment.activity?.registerActivityLifecycleCallbacks(
            object : MockActivityLifecycleCallbacks() {
                override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                    (activity as AccountActivity).accountManager = accountManager
                }
            })

        fragmentManager = activity.supportFragmentManager
        fragmentManager.registerFragmentLifecycleCallbacks(
            object : FragmentManager.FragmentLifecycleCallbacks() {
                override fun onFragmentCreated(
                    fm: FragmentManager,
                    f: Fragment,
                    savedInstanceState: Bundle?
                ) {
                    if (f is SignInFragment) {
                        f.viewModel = viewModel
                    }
                }
            }, false)
        fragmentManager.beginTransaction().add(R.id.fragment_container, fragment).commit()
    }

    @Test
    fun submitDisabledByDefault() {
        onView(withId(R.id.button_submit)).check(matches(not(isProcessing())))
        onView(withId(R.id.button_submit)).check(matches(not(isEnabled())))
    }

    @Test
    fun emptyFields_submitDisabled() {
        // 输入空内容，测试按钮是否是禁用状态
        onView(withId(R.id.edit_text_username)).perform(typeText(" "))
        onView(withId(R.id.edit_text_password)).perform(typeText(" "))

        onView(withId(R.id.button_submit)).check(matches(not(isEnabled())))
    }

    @Test
    fun nonEmptyFields_submitEnabled() {
        // 输入非空的内容，测试按钮是否是启用状态
        onView(withId(R.id.edit_text_username)).perform(typeText("foo"))
        onView(withId(R.id.edit_text_password)).perform(typeText("bar"))

        onView(withId(R.id.button_submit)).check(matches(isEnabled()))
    }

    @Test
    fun formPost_submitButtonProcessing() {
        `when`(userRepository.signIn(anyString(), anyString()))
            .thenReturn(Observable.just(Result.Success(TestData.userData)))

        onView(withId(R.id.edit_text_username)).perform(typeText("foo"))
        onView(withId(R.id.edit_text_password)).perform(typeText("bar"))
        onView(withId(R.id.button_submit)).perform(click())

        onView(withId(R.id.button_submit)).check(matches(isProcessing()))
    }

    @Test
    fun failureResult_toastResponseMessage() {
        // 返回失败数据，测试是否弹出提示
        `when`(userRepository.signIn(anyString(), anyString()))
            .thenReturn(Observable.just(Result.Failure("账号密码不匹配")))

        onView(withId(R.id.edit_text_username)).perform(typeText("foo"))
        onView(withId(R.id.edit_text_password)).perform(typeText("bar"))
        onView(withId(R.id.button_submit)).perform(click())

        assertThat(shadowOf(activity.application).shownToasts).hasSize(1)
        assertThat(ShadowToast.getTextOfLatestToast()).contains("账号密码不匹配")
    }

    @Test
    fun errorResult_toastErrorOccurred() {
        // 返回错误数据，测试是否弹出“出错了”的提示
        `when`(userRepository.signIn(anyString(), anyString()))
            .thenReturn(Observable.just(Result.Error("something wrong")))

        onView(withId(R.id.edit_text_username)).perform(typeText("foo"))
        onView(withId(R.id.edit_text_password)).perform(typeText("bar"))
        onView(withId(R.id.button_submit)).perform(click())

        assertThat(shadowOf(activity.application).shownToasts).hasSize(1)
        assertThat(ShadowToast.getTextOfLatestToast())
            .contains(activity.getString(R.string.error_occurred_message))
    }

    @Test
    fun successResult_activityFinish() {
        // 返回登录成功的数据，测试当前 activity 是否被关闭
        `when`(userRepository.signIn(anyString(), anyString()))
            .thenReturn(Observable.just(Result.Success(TestData.userData)))
        `when`(accountManager.addAccountExplicitly(any(), anyString(), any())).thenReturn(true)
        doNothing().`when`(accountManager).setAuthToken(any(), anyString(), anyString())

        onView(withId(R.id.edit_text_username)).perform(typeText("foo"))
        onView(withId(R.id.edit_text_password)).perform(typeText("bar"))
        onView(withId(R.id.button_submit)).perform(click())

        assertThat(activity.isFinishing).isTrue()
    }

    @Test
    fun signUpTextClicked_switchFragment() {
        // 点击注册提示文本，检查是否跳转到注册 Fragment
        onView(withId(R.id.text_view_sign_up_hint)).perform(click())

        assertThat(fragmentManager.findFragmentById(R.id.fragment_container))
            .isInstanceOf(SignUpFragment::class.java)
    }
}