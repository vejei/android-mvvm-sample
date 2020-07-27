package com.example.wanandroid.settings

import androidx.lifecycle.ViewModel
import com.example.wanandroid.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class SettingsModule {

    @ContributesAndroidInjector
    abstract fun contributeHistoryDisableAlertDialogFragment(): HistoryDisableAlertDialogFragment

    @Binds
    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    abstract fun bindSettingsViewModel(viewModel: SettingsViewModel): ViewModel
}