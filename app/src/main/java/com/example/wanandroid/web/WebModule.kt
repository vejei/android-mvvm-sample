package com.example.wanandroid.web

import androidx.lifecycle.ViewModel
import com.example.wanandroid.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class WebModule {
    @Binds
    @IntoMap
    @ViewModelKey(WebViewModel::class)
    abstract fun bindWebViewModel(viewModel: WebViewModel): ViewModel
}