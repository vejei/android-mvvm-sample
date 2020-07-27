package com.example.wanandroid

import android.view.View
import androidx.test.espresso.matcher.BoundedMatcher
import com.example.uikit.ProgressButton
import com.google.android.material.textfield.TextInputLayout
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.core.Is.`is`

class IsProcessingMatcher : BoundedMatcher<View, ProgressButton>(
    ProgressButton::class.java) {
    override fun describeTo(description: Description?) {
        description?.appendText("is processing")
    }

    override fun matchesSafely(item: ProgressButton?): Boolean {
        return item?.isProcessing ?: false
    }

    companion object {
        fun isProcessing(): Matcher<View> {
            return IsProcessingMatcher()
        }
    }
}

class IsErrorEnabledMatcher : BoundedMatcher<View, TextInputLayout>(TextInputLayout::class.java) {
    override fun describeTo(description: Description?) {
        description?.appendText("is error enabled")
    }

    override fun matchesSafely(item: TextInputLayout?): Boolean {
        return item?.isErrorEnabled ?: false
    }

    companion object {
        fun isErrorEnabled(): Matcher<View> {
            return IsErrorEnabledMatcher()
        }
    }
}

class HasErrorTextMatcher(expectedError: String) : BoundedMatcher<View, TextInputLayout>(TextInputLayout::class.java) {
    private var stringMatcher: Matcher<String> = `is`(expectedError)

    override fun describeTo(description: Description?) {
        description?.appendText("with error: ")
        stringMatcher.describeTo(description)
    }

    override fun matchesSafely(item: TextInputLayout?): Boolean {
        return stringMatcher.matches(item?.error)
    }

    companion object {
        @JvmStatic
        fun hasErrorText(expectedError: String): Matcher<View> {
            return HasErrorTextMatcher(expectedError)
        }
    }
}