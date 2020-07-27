package com.example.wanandroid.main.project

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ProjectModule {

    @ContributesAndroidInjector
    abstract fun contributeProjectPageFragment(): ProjectPageFragment
}