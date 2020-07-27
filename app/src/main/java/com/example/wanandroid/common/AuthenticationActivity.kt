package com.example.wanandroid.common

import android.accounts.Account
import android.accounts.AccountManager
import android.os.Bundle
import com.example.wanandroid.R
import com.example.wanandroid.account.AccountAuthenticator
import com.example.wanandroid.utils.shortMessage
import javax.inject.Inject

abstract class AuthenticationActivity : InjectableActivity() {
    @Inject lateinit var accountManager: AccountManager

    open fun requestAccount() {
        val accounts = accountManager.getAccountsByType(getString(R.string.account_type))
        if (accounts.isEmpty()) {
            onUnauthorized()
        } else {
            onAuthenticated(accounts.first())
        }
    }

    open fun onUnauthorized() {
        shortMessage(getString(R.string.not_logged_in_toast_message))
        accountManager.addAccount(
            getString(R.string.account_type), null, null,
            Bundle().apply {
                putString(AccountAuthenticator.KEY_FRAGMENT_TYPE, AccountAuthenticator.SIGN_IN_FRAGMENT)
            }, this, null, null
        )
    }

    open fun onAuthenticated(account: Account) {}
}