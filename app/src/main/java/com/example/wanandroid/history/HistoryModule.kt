package com.example.wanandroid.history

import androidx.lifecycle.ViewModel
import com.example.wanandroid.di.ViewModelKey
import com.example.wanandroid.settings.HistoryDisableAlertDialogFragment
import com.example.wanandroid.settings.SettingsViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class HistoryModule {
    @ContributesAndroidInjector
    abstract fun contributeHistoryFragment(): HistoryFragment

    @ContributesAndroidInjector
    abstract fun contributeHistoryDisableAlertDialogFragment(): HistoryDisableAlertDialogFragment

    @Binds
    @IntoMap
    @ViewModelKey(HistoryViewModel::class)
    abstract fun bindHistoryViewModel(viewModel: HistoryViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    abstract fun bindSettingsViewModel(viewModel: SettingsViewModel): ViewModel
}