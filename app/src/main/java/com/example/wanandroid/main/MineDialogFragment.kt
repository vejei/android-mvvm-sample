package com.example.wanandroid.main

import android.Manifest
import android.accounts.Account
import android.accounts.AccountManager
import android.accounts.AccountManagerCallback
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.wanandroid.R
import com.example.wanandroid.account.AccountActivity.Companion.KEY_EMAIL
import com.example.wanandroid.account.AccountActivity.Companion.KEY_NICKNAME_NAME
import com.example.wanandroid.account.AccountActivity.Companion.KEY_PUBLIC_NAME
import com.example.wanandroid.account.AccountAuthenticator.Companion.KEY_ACCOUNT_TYPE
import com.example.wanandroid.account.AccountAuthenticator.Companion.KEY_AUTH_TOKEN_TYPE
import com.example.wanandroid.account.AccountAuthenticator.Companion.KEY_FRAGMENT_TYPE
import com.example.wanandroid.account.AccountAuthenticator.Companion.SIGN_IN_FRAGMENT
import com.example.wanandroid.account.AccountAuthenticator.Companion.SIGN_UP_FRAGMENT
import com.example.wanandroid.databinding.FragmentMineDialogBinding
import com.example.wanandroid.history.HistoryActivity
import com.example.wanandroid.mark.MarkActivity
import com.example.wanandroid.settings.SettingsActivity
import com.example.wanandroid.todo.TodoActivity
import com.example.wanandroid.utils.Transformers.throttleClick
import com.example.wanandroid.utils.shortMessage
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.jakewharton.rxbinding3.view.clicks
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import dagger.android.support.AndroidSupportInjection
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber
import javax.inject.Inject

class MineDialogFragment : BottomSheetDialogFragment(), HasAndroidInjector, EasyPermissions.PermissionCallbacks {
    @Inject lateinit var androidInjector: DispatchingAndroidInjector<Any>
    private lateinit var binding: FragmentMineDialogBinding
    @Inject lateinit var accountManager: AccountManager
    @Inject lateinit var disposables: CompositeDisposable
    private lateinit var authenticateSuccessCallback: AccountManagerCallback<Bundle>
    private lateinit var dialogCallback: AuthenticationCallback
    private lateinit var markCallback: AuthenticationCallback
    private lateinit var todoCallback: AuthenticationCallback

    override fun androidInjector(): AndroidInjector<Any> {
        return androidInjector
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == DIALOG_GET_ACCOUNTS || requestCode == MARK_GET_ACCOUNTS
            || TODO_GET_ACCOUNTS == TODO_GET_ACCOUNTS) {
            shortMessage(getString(R.string.get_accounts_permission_denied_toast_message))
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        val callback = when (requestCode) {
            DIALOG_GET_ACCOUNTS -> dialogCallback
            MARK_GET_ACCOUNTS -> markCallback
            TODO_GET_ACCOUNTS -> todoCallback
            else -> null
        }
        if (callback != null) requestAccount(callback)
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMineDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.attributes?.windowAnimations = R.style.MineDialogAnimation
    }

    override fun getTheme(): Int = R.style.RoundedCornerBottomSheetDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialogCallback = object : AuthenticationCallback {
            override fun onUnauthorized() {
                setupUnauthenticatedView()
            }

            override fun onAuthenticated(account: Account) {
                setupProfileView(account)
            }
        }

        markCallback = object : AuthenticationCallback {
            override fun onUnauthorized() {
                startAccountActivity()
            }

            override fun onAuthenticated(account: Account) {
                startActivity(Intent(context, MarkActivity::class.java))
            }
        }

        todoCallback = object : AuthenticationCallback {
            override fun onUnauthorized() {
                startAccountActivity()
            }

            override fun onAuthenticated(account: Account) {
                startActivity(Intent(context, TodoActivity::class.java))
            }
        }

        checkPlatformVersion(DIALOG_GET_ACCOUNTS, dialogCallback)

        authenticateSuccessCallback = AccountManagerCallback { future ->
            if (!future.isCancelled) {
                val result = future?.result
                if (result?.containsKey(KEY_ACCOUNT_TYPE) == true
                    && result.containsKey(KEY_AUTH_TOKEN_TYPE)) {
                    shortMessage(getString(R.string.mine_dialog_logged_in_toast_message))
                    setupProfileView(
                        accountManager.getAccountsByType(result.getString(KEY_ACCOUNT_TYPE)).first()
                    )
                }
            }
        }

        with(binding) {

            textViewHistory.clicks().compose(throttleClick()).subscribe {
                // 跳转到浏览历史（不用登录）
                startActivity(Intent(context, HistoryActivity::class.java))
                dismiss()
            }.addTo(disposables)

            textViewMark.clicks().compose(throttleClick()).subscribe {
                // 跳转到收藏
                checkPlatformVersion(MARK_GET_ACCOUNTS, markCallback)
                dismiss()
            }.addTo(disposables)

            textViewTodo.clicks().compose(throttleClick()).subscribe {
                // 跳转到待办
                checkPlatformVersion(TODO_GET_ACCOUNTS, todoCallback)
                dismiss()
            }.addTo(disposables)

            textViewSettings.clicks().compose(throttleClick()).subscribe {
                // 跳转到设置（不用登录）
                startActivity(Intent(context, SettingsActivity::class.java))
                dismiss()
            }.addTo(disposables)
        }
    }

    private fun requestAccount(callback: AuthenticationCallback) {
        val accounts = accountManager.getAccountsByType(getString(R.string.account_type))
        if (accounts.isEmpty()) {
            // 还没有登录
            callback.onUnauthorized()
        } else {
            // 已经登录
            callback.onAuthenticated(accounts.first())
        }
    }

    private fun setupUnauthenticatedView() {
        Timber.d("onUnauthorized()")
        with(binding) {
            viewStubHeader.layoutResource = R.layout.layout_unauthenticated
            val headerView = viewStubHeader.inflate()
            val signInButton = headerView.findViewById<MaterialButton>(R.id.button_sign_in)
            val signUpButton = headerView.findViewById<MaterialButton>(R.id.button_sign_up)

            // 跳转到登录
            signInButton.clicks().compose(throttleClick()).subscribe {
                accountManager.addAccount(
                    getString(R.string.account_type), null, null,
                    Bundle().apply { putString(KEY_FRAGMENT_TYPE, SIGN_IN_FRAGMENT) }, activity,
                    authenticateSuccessCallback, null
                )
                dismiss()
            }.addTo(disposables)

            // 跳转到注册
            signUpButton.clicks().compose(throttleClick()).subscribe {
                accountManager.addAccount(
                    getString(R.string.account_type), null, null,
                    Bundle().apply { putString(KEY_FRAGMENT_TYPE, SIGN_UP_FRAGMENT) }, activity,
                    authenticateSuccessCallback, null)
                dismiss()
            }.addTo(disposables)
        }
    }

    private fun setupProfileView(account: Account) {
        Timber.d("onAuthenticated(account: $account)")

        binding.viewStubHeader.layoutResource = R.layout.layout_user_profile
        val headerView = binding.viewStubHeader.inflate()

        val username = account.name
        val email = accountManager.getUserData(account, KEY_EMAIL)
        val nickname = accountManager.getUserData(account, KEY_NICKNAME_NAME)
        val publicName = accountManager.getUserData(account, KEY_PUBLIC_NAME)

        val nameTextView = headerView.findViewById<TextView>(R.id.text_view_name)
        val emailTextView = headerView.findViewById<TextView>(R.id.text_view_email)
        val signOutButton = headerView.findViewById<MaterialButton>(R.id.button_sign_out)

        if (nickname.isNotEmpty()) {
            nameTextView.text = nickname
        } else {
            if (publicName.isNotEmpty()) {
                nameTextView.text = publicName
            } else {
                nameTextView.text = username
            }
        }
        emailTextView.text = if (email.isNotEmpty()) email else username

        signOutButton.clicks().compose(throttleClick()).subscribe {
            dismiss()
            SignOutDialogFragment().show(requireActivity().supportFragmentManager,
                SignOutDialogFragment.SHOW_TAG)
        }.addTo(disposables)
    }

    private fun checkPlatformVersion(requestCode: Int, callback: AuthenticationCallback) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (EasyPermissions.hasPermissions(requireContext(), Manifest.permission.GET_ACCOUNTS)) {
                requestAccount(callback)
            } else {
                EasyPermissions.requestPermissions(
                    this, getString(R.string.get_accounts_permission_request_rationale_message),
                    requestCode, Manifest.permission.GET_ACCOUNTS
                )
            }
        } else {
            requestAccount(callback)
        }
    }

    private fun startAccountActivity() {
        accountManager.addAccount(
            getString(R.string.account_type), null, null,
            Bundle().apply { putString(KEY_FRAGMENT_TYPE, SIGN_IN_FRAGMENT) }, activity,
            null, null
        )
    }

    override fun onDestroy() {
        disposables.dispose()
        super.onDestroy()
    }

    companion object {
        const val SHOW_TAG = "mine_dialog"

        const val DIALOG_GET_ACCOUNTS = 1
        const val MARK_GET_ACCOUNTS = 2
        const val TODO_GET_ACCOUNTS = 3
    }
}

interface AuthenticationCallback {
    fun onUnauthorized()
    fun onAuthenticated(account: Account)
}