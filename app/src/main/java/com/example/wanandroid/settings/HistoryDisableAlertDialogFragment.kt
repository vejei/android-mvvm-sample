package com.example.wanandroid.settings

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.wanandroid.R
import com.example.wanandroid.databinding.FragmentHistoryDisableAlertDialogBinding
import com.example.wanandroid.utils.viewModelProvider
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import dagger.android.support.AndroidSupportInjection
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class HistoryDisableAlertDialogFragment : DialogFragment(), HasAndroidInjector {
    @Inject lateinit var androidInjector: DispatchingAndroidInjector<Any>
    private lateinit var binding: FragmentHistoryDisableAlertDialogBinding
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: SettingsViewModel
    @Inject lateinit var disposables: CompositeDisposable
    @Inject lateinit var preferences: SharedPreferences

    override fun androidInjector(): AndroidInjector<Any> {
        return androidInjector
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
        viewModel = viewModelProvider(viewModelFactory)
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
        dialog?.setTitle(R.string.history_disable_alert_dialog_title)
        binding = FragmentHistoryDisableAlertDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            buttonOk.setOnClickListener {
                preferences.edit().putBoolean(getString(R.string.settings_enable_history_key), false).apply()
                viewModel.onHistoriesClear()
                targetFragment?.onActivityResult(DISABLE_HISTORY, Activity.RESULT_OK, Intent().apply {
                    putExtra(KEY_IS_DISABLE, true)
                })
                requireActivity().invalidateOptionsMenu()
                dismiss()
            }
            buttonCancel.setOnClickListener {
                targetFragment?.onActivityResult(DISABLE_HISTORY, Activity.RESULT_OK, Intent().apply {
                    putExtra(KEY_IS_DISABLE, false)
                })
                dismiss()
            }
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        targetFragment?.onActivityResult(DISABLE_HISTORY, Activity.RESULT_OK, Intent().apply {
            putExtra(KEY_IS_DISABLE, false)
        })
        super.onCancel(dialog)
    }

    companion object {
        const val SHOW_TAG = "history_disable_alert_dialog"

        const val DISABLE_HISTORY = 1
        const val KEY_IS_DISABLE = "is_disable"
    }
}