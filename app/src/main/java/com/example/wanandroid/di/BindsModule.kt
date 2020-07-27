package com.example.wanandroid.di

import android.app.Application
import android.content.Context
import dagger.Binds
import dagger.Module

@Module
abstract class BindsModule {
    @Binds
    abstract fun bindContext(application: Application): Context
}