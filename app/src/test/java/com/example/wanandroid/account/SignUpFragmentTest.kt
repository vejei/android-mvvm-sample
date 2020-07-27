package com.example.wanandroid.account

import android.accounts.AccountManager
import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.example.wanandroid.HasErrorTextMatcher.Companion.hasErrorText
import com.example.wanandroid.IsErrorEnabledMatcher.Companion.isErrorEnabled
import com.example.wanandroid.IsProcessingMatcher.Companion.isProcessing
import com.example.wanandroid.MockActivityLifecycleCallbacks
import com.example.wanandroid.R
import com.example.wanandroid.TestData
import com.example.wanandroid.data.Result
import com.example.wanandroid.data.UserRepository
import com.google.common.truth.Truth.assertThat
import io.reactivex.Observable
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.doNothing
import org.mockito.MockitoAnnotations
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.shadows.ShadowToast

@RunWith(RobolectricTestRunner::class)
class SignUpFragmentTest {
    private lateinit var activity: AccountActivity
    @Mock private lateinit var accountManager: AccountManager
    private var fragment: SignUpFragment = SignUpFragment()
    private lateinit var fragmentManager: FragmentManager

    private lateinit var viewModel: SignUpViewModel
    @Mock private lateinit var userRepository: UserRepository

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        viewModel = SignUpViewModel(userRepository)

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
                    if (f is SignUpFragment) {
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
        // 表单字段为空时（输入空白字符时也是空），提交按钮应该是禁用状态
        onView(withId(R.id.edit_text_username)).perform(typeText(" "))
        onView(withId(R.id.edit_text_password)).perform(typeText(" "))
        onView(withId(R.id.edit_text_password_repeat)).perform(typeText(" "))

        onView(withId(R.id.button_submit)).check(matches(not(isEnabled())))
    }

    @Test
    fun nonEmptyFields_submitEnabled() {
        // 字段不为空，且密码一致按钮应该是启用状态
        onView(withId(R.id.edit_text_username)).perform(typeText("foo"))
        onView(withId(R.id.edit_text_password)).perform(typeText("bar"))
        onView(withId(R.id.edit_text_password_repeat)).perform(typeText("bar"))

        onView(withId(R.id.button_submit)).check(matches(isEnabled()))
    }

    @Test
    fun passwordInconsistent_submitDisabled() {
        // 密码不一致时，提交按钮应该是禁用状态
        onView(withId(R.id.edit_text_username)).perform(typeText("foo"))
        onView(withId(R.id.edit_text_password)).perform(typeText("bar"))
        onView(withId(R.id.edit_text_password_repeat)).perform(typeText("foobar"))

        onView(withId(R.id.button_submit)).check(matches(not(isEnabled())))
    }

    @Test
    fun passwordInconsistent_errorEnabled() {
        onView(withId(R.id.edit_text_username)).perform(typeText("foo"))
        onView(withId(R.id.edit_text_password)).perform(typeText("bar"))
        onView(withId(R.id.edit_text_password_repeat)).perform(typeText("foobar"))

        onView(withId(R.id.text_input_layout_password_repeat)).check(matches(isErrorEnabled()))
        onView(withId(R.id.text_input_layout_password_repeat)).check(matches(
            hasErrorText(activity.getString(R.string.sign_up_password_not_match_error_message))))
    }

    @Test
    fun formPost_submitButtonProcessing() {
        // 提交表单后，按钮应该处于处理中的状态
        `when`(userRepository.signUp(anyString(), anyString(), anyString()))
            .thenReturn(Observable.just(Result.Success(TestData.userData)))

        onView(withId(R.id.edit_text_username)).perform(typeText("foo"))
        onView(withId(R.id.edit_text_password)).perform(typeText("bar"))
        onView(withId(R.id.edit_text_password_repeat)).perform(typeText("bar"))
        onView(withId(R.id.button_submit)).perform(click())

        onView(withId(R.id.button_submit)).check(matches(isProcessing()))
    }

    @Test
    fun failureResult_toastResponseMessage() {
        `when`(userRepository.signUp(anyString(), anyString(), anyString()))
            .thenReturn(Observable.just(Result.Failure("账号已存在")))

        onView(withId(R.id.edit_text_username)).perform(typeText("foo"))
        onView(withId(R.id.edit_text_password)).perform(typeText("bar"))
        onView(withId(R.id.edit_text_password_repeat)).perform(typeText("bar"))
        onView(withId(R.id.button_submit)).perform(click())

        assertThat(Shadows.shadowOf(activity.application).shownToasts).hasSize(1)
        assertThat(ShadowToast.getTextOfLatestToast()).contains("账号已存在")
    }

    @Test
    fun errorResult_toastErrorOccurred() {
        `when`(userRepository.signUp(anyString(), anyString(), anyString()))
            .thenReturn(Observable.just(Result.Error("something wrong")))

        onView(withId(R.id.edit_text_username)).perform(typeText("foo"))
        onView(withId(R.id.edit_text_password)).perform(typeText("bar"))
        onView(withId(R.id.edit_text_password_repeat)).perform(typeText("bar"))
        onView(withId(R.id.button_submit)).perform(click())

        assertThat(Shadows.shadowOf(activity.application).shownToasts).hasSize(1)
        assertThat(ShadowToast.getTextOfLatestToast())
            .contains(activity.getString(R.string.error_occurred_message))
    }

    @Test
    fun successResult_activityFinish() {
        // 返回登录成功的数据，测试当前 activity 是否被关闭
        `when`(userRepository.signUp(anyString(), anyString(), anyString()))
            .thenReturn(Observable.just(Result.Success(TestData.userData)))
        `when`(accountManager.addAccountExplicitly(any(), anyString(), any())).thenReturn(true)
        doNothing().`when`(accountManager).setAuthToken(any(), anyString(), anyString())

        onView(withId(R.id.edit_text_username)).perform(typeText("foo"))
        onView(withId(R.id.edit_text_password)).perform(typeText("bar"))
        onView(withId(R.id.edit_text_password_repeat)).perform(typeText("bar"))
        onView(withId(R.id.button_submit)).perform(click())

        assertThat(activity.isFinishing).isTrue()
    }

    @Test
    fun signInTextClicked_switchFragment() {
        fragment.view?.findViewById<TextView>(R.id.text_view_sign_in_hint)?.performClick()
        assertThat(fragmentManager.findFragmentById(R.id.fragment_container))
            .isInstanceOf(SignInFragment::class.java)
    }
}