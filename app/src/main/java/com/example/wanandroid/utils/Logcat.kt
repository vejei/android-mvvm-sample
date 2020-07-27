package com.example.wanandroid.utils

import android.util.Log
import com.example.wanandroid.BuildConfig
import timber.log.Timber

object Logcat {
    fun init() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(object : Timber.Tree() {
                override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                    if (priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO) {
                        return
                    }
                    if (t != null) {
                        if (priority == Log.ERROR) {
                            CrashReporter.logError(t)
                        } else if (priority == Log.WARN) {
                            CrashReporter.logWarning(t)
                        }
                    }
                }
            })
        }
    }
}

object CrashReporter {
    fun log(priority: Int, tag: String, message: String) {}

    fun logWarning(throwable: Throwable) {
        Timber.e(throwable)
    }

    fun logError(throwable: Throwable) {
        Timber.e(throwable)
    }
}