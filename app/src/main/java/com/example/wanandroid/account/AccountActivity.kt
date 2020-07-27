package com.example.wanandroid.account

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.example.wanandroid.R
import com.example.wanandroid.account.AccountAuthenticator.Companion.KEY_ACCOUNT_TYPE
import com.example.wanandroid.account.AccountAuthenticator.Companion.KEY_AUTH_TOKEN_TYPE
import com.example.wanandroid.account.AccountAuthenticator.Companion.KEY_FRAGMENT_TYPE
import com.example.wanandroid.account.AccountAuthenticator.Companion.KEY_IS_NEW_ACCOUNT
import com.example.wanandroid.account.AccountAuthenticator.Companion.SIGN_IN_FRAGMENT
import com.example.wanandroid.account.AccountAuthenticator.Companion.SIGN_UP_FRAGMENT
import com.example.wanandroid.data.UserData
import com.example.wanandroid.main.MainActivity
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import timber.log.Timber
import javax.inject.Inject

class AccountActivity : AppCompatAccountAuthenticatorActivity(), HasAndroidInjector {
    @Inject lateinit var androidInjector: DispatchingAndroidInjector<Any>
    @Inject lateinit var accountManager: AccountManager

    private var isNewAccount: Boolean = true
    private var accountType: String? = null
    private var accountTokenType: String? = null
    private lateinit var closeButton: ImageButton

    override fun androidInjector(): AndroidInjector<Any> {
        return androidInjector
    }

    override fun onCreate(icicle: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(icicle)

        if (accountManager.getAccountsByType(getString(R.string.account_type)).isNotEmpty()) {
            startActivity(
                Intent(this@AccountActivity, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            )
        }

        setContentView(R.layout.activity_account)

        isNewAccount = intent.getBooleanExtra(KEY_IS_NEW_ACCOUNT, true)
        accountType = intent.getStringExtra(KEY_ACCOUNT_TYPE)
        accountTokenType = intent.getStringExtra(KEY_AUTH_TOKEN_TYPE)

        val fragment: Fragment? = when(intent.getStringExtra(KEY_FRAGMENT_TYPE)) {
            SIGN_IN_FRAGMENT -> SignInFragment()
            SIGN_UP_FRAGMENT -> SignUpFragment()
            else -> null
        }
        if (icicle == null && fragment != null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, fragment, fragment.javaClass.simpleName)
                .commit()
        }

        closeButton = findViewById(R.id.button_account_close)
        closeButton.setOnClickListener {
            finish()
        }
    }

    fun addAccount(response: UserData?, password: String): Bundle {
        Timber.d("authenticate response: $response")
        val account = Account(response?.username, getString(R.string.account_type))
        val userData = if (response != null) {
            Bundle().apply {
                putString(KEY_EMAIL, response.email)
                putString(KEY_NICKNAME_NAME, response.nickname)
                putString(KEY_PUBLIC_NAME, response.publicName)
            }
        } else {
            null
        }
//        accountManager.addAccountExplicitly(account, response?.password, userData)
        accountManager.addAccountExplicitly(account, password, userData)
        return Bundle().apply {
            putString(AccountManager.KEY_ACCOUNT_NAME, account.name)
            putString(AccountManager.KEY_ACCOUNT_TYPE, account.type)
            putString(AccountManager.KEY_AUTHTOKEN, response?.token)
        }
    }

    fun toSignIn() {
        Timber.d("fragment size: ${supportFragmentManager.fragments.size}")
        Timber.d("toSignIn")
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.fragment_fade_in, R.anim.fragment_fade_out)
            .replace(R.id.fragment_container, SignInFragment())
            .commit()
    }

    fun toSignUp() {
        Timber.d("fragment size: ${supportFragmentManager.fragments.size}")
        Timber.d("toSignUp")
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.fragment_fade_in, R.anim.fragment_fade_out)
            .replace(R.id.fragment_container, SignUpFragment())
            .commit()
    }

    companion object {
        const val KEY_EMAIL = "email"
        const val KEY_PUBLIC_NAME = "public_name"
        const val KEY_NICKNAME_NAME = "nickname"
    }
}