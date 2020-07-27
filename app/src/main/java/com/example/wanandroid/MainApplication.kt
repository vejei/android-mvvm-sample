package com.example.wanandroid

import android.app.Application
import com.example.wanandroid.di.DaggerAppComponent
import com.example.wanandroid.utils.Logcat
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

class MainApplication : Application(), HasAndroidInjector {
    @Inject lateinit var androidInjector: DispatchingAndroidInjector<Any>

    override fun onCreate() {
        super.onCreate()

        DaggerAppComponent.builder()
            .application(this)
            .build()
            .inject(this)

        Logcat.init()
    }

    override fun androidInjector(): AndroidInjector<Any> {
        return androidInjector
    }
}