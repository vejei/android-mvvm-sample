package com.example.wanandroid.account

import android.accounts.Account
import android.accounts.AccountManager
import android.accounts.AccountManager.KEY_ACCOUNT_NAME
import android.accounts.AccountManager.KEY_AUTHTOKEN
import android.app.Application
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.example.wanandroid.R
import com.example.wanandroid.TestData
import com.example.wanandroid.account.AccountAuthenticator.Companion.KEY_ACCOUNT_TYPE
import com.example.wanandroid.account.AccountAuthenticator.Companion.KEY_FRAGMENT_TYPE
import com.example.wanandroid.account.AccountAuthenticator.Companion.SIGN_IN_FRAGMENT
import com.example.wanandroid.main.MainActivity
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class AccountActivityTest {
    private lateinit var context: Application
    private lateinit var accountManager: AccountManager
    private lateinit var accountType: String

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        accountType = context.getString(R.string.account_type)
        accountManager = AccountManager.get(context)
    }

    @Test
    fun hasAccount_startMainActivity() {
        accountManager.addAccountExplicitly(Account("test", accountType), null, null)
        ActivityScenario.launch(AccountActivity::class.java)

        val expectedIntent = Intent(context, MainActivity::class.java)
        val actualIntent = shadowOf(context).nextStartedActivity

        assertThat(expectedIntent.component).isEqualTo(actualIntent.component)
    }

    @Test
    fun noAccount_enterSignInScreen() {
        val intent = Intent(context, AccountActivity::class.java).apply {
            putExtra(KEY_FRAGMENT_TYPE, SIGN_IN_FRAGMENT)
        }
        val scenario = ActivityScenario.launch<AccountActivity>(intent)

        assertThat(accountManager.getAccountsByType(accountType).size).isEqualTo(0)
        scenario.onActivity {
            val fragment = it.supportFragmentManager.findFragmentById(R.id.fragment_container)
            assertThat(fragment).isInstanceOf(SignInFragment::class.java)
        }.close()
    }

    @Test
    fun closeButtonClicked_finishActivity() {
        val scenario = ActivityScenario.launch(AccountActivity::class.java)

        onView(withId(R.id.button_account_close)).perform(click())

        scenario.onActivity {
            assertThat(it.isFinishing).isTrue()
        }.close()
    }

    @Test
    fun addAccount() {
        val scenario = ActivityScenario.launch(AccountActivity::class.java)

        scenario.onActivity {
            val bundle = it.addAccount(TestData.userData, "")

            assertThat(accountManager.getAccountsByType(accountType).size).isEqualTo(1)
            assertThat(bundle.getString(KEY_ACCOUNT_NAME)).contains(TestData.userData.username)
            assertThat(bundle.getString(KEY_ACCOUNT_TYPE)).contains(accountType)
            assertThat(bundle.getString(KEY_AUTHTOKEN)).contains(TestData.userData.token)
        }.close()
    }
}