package com.example.wanandroid.account

import android.accounts.AbstractAccountAuthenticator
import android.accounts.Account
import android.accounts.AccountAuthenticatorResponse
import android.accounts.AccountManager
import android.accounts.AccountManager.KEY_BOOLEAN_RESULT
import android.content.Context
import android.content.Intent
import android.os.Bundle

class AccountAuthenticator(private val context: Context) : AbstractAccountAuthenticator(context) {

    override fun confirmCredentials(
        response: AccountAuthenticatorResponse?,
        account: Account?,
        options: Bundle?
    ): Bundle? {
        return null
    }

    override fun editProperties(
        response: AccountAuthenticatorResponse?,
        accountType: String?
    ): Bundle? {
        return null
    }

    override fun hasFeatures(
        response: AccountAuthenticatorResponse?,
        account: Account?,
        features: Array<out String>?
    ): Bundle {
        return Bundle().apply { putBoolean(KEY_BOOLEAN_RESULT, false) }
    }

    override fun getAuthTokenLabel(authTokenType: String?): String? {
        return null
    }

    override fun updateCredentials(
        response: AccountAuthenticatorResponse?,
        account: Account?,
        authTokenType: String?,
        options: Bundle?
    ): Bundle? {
        return null
    }

    override fun getAuthToken(
        response: AccountAuthenticatorResponse?,
        account: Account?,
        authTokenType: String?,
        options: Bundle?
    ): Bundle? {
        return null
    }

    override fun addAccount(
        response: AccountAuthenticatorResponse?,
        accountType: String?,
        authTokenType: String?,
        requiredFeatures: Array<out String>?,
        options: Bundle?
    ): Bundle {
        val intent = Intent(context, AccountActivity::class.java).apply {
            putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)
            putExtra(KEY_ACCOUNT_TYPE, accountType)
            putExtra(KEY_AUTH_TOKEN_TYPE, authTokenType)
            putExtra(KEY_IS_NEW_ACCOUNT, true)
            putExtra(KEY_FRAGMENT_TYPE, SIGN_IN_FRAGMENT)
        }
        return Bundle().apply {
            putParcelable(AccountManager.KEY_INTENT, intent)
        }
    }

    companion object {
        const val KEY_ACCOUNT_TYPE = "accountType"
        const val KEY_AUTH_TOKEN_TYPE = "authTokenType"
        const val KEY_IS_NEW_ACCOUNT = "isNewAccount"
        const val KEY_FRAGMENT_TYPE = "fragment_type"
        const val SIGN_IN_FRAGMENT = "sign_in_fragment"
        const val SIGN_UP_FRAGMENT = "sign_up_fragment"
    }
}