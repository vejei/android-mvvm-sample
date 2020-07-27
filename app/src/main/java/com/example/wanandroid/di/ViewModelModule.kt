package com.example.wanandroid.di

import androidx.lifecycle.ViewModelProvider
import com.example.wanandroid.ViewModelFactory
import dagger.Binds
import dagger.Module

@Module
abstract class ViewModelModule {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}