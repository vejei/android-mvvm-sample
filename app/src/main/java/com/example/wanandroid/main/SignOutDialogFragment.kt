package com.example.wanandroid.main

import android.accounts.AccountManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.wanandroid.R
import com.example.wanandroid.databinding.FragmentSignOutDialogBinding
import com.example.wanandroid.utils.errorMessage
import com.example.wanandroid.utils.shortMessage
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class SignOutDialogFragment : DialogFragment(), HasAndroidInjector {
    @Inject lateinit var androidInjector: DispatchingAndroidInjector<Any>
    private lateinit var binding: FragmentSignOutDialogBinding
    @Inject lateinit var accountManager: AccountManager

    override fun androidInjector(): AndroidInjector<Any> {
        return androidInjector
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog_Alert)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.setTitle(R.string.sign_out_dialog_title)
        binding = FragmentSignOutDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.attributes?.windowAnimations = R.style.DialogAnimation
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val accounts = accountManager.getAccountsByType(getString(R.string.account_type))
        if (accounts.isEmpty()) return
        val account = accounts[0]

        with(binding) {
            buttonCancel.setOnClickListener { dismiss() }
            buttonOk.setOnClickListener {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1) {
                    accountManager.removeAccount(account, {
                        if (!it.isCancelled) {
                            onSignOut(it.result)
                        }
                    }, null)
                } else {
                    onSignOut(accountManager.removeAccountExplicitly(account))
                }
                dismiss()
            }
        }
    }

    private fun onSignOut(result: Boolean) {
        if (result) {
            shortMessage(getString(R.string.mine_dialog_sign_out_toast_message))
        } else {
            errorMessage()
        }
    }

    companion object {
        const val SHOW_TAG = "sign_out_dialog"
    }
}