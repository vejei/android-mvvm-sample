package com.example.wanandroid.di

import com.example.wanandroid.account.AccountActivity
import com.example.wanandroid.account.AccountModule
import com.example.wanandroid.history.HistoryActivity
import com.example.wanandroid.history.HistoryModule
import com.example.wanandroid.main.MainActivity
import com.example.wanandroid.main.MainModule
import com.example.wanandroid.main.category.CategoryActivity
import com.example.wanandroid.main.category.CategoryModule
import com.example.wanandroid.search.SearchActivity
import com.example.wanandroid.search.SearchModule
import com.example.wanandroid.mark.MarkActivity
import com.example.wanandroid.mark.MarkModule
import com.example.wanandroid.settings.SettingsActivity
import com.example.wanandroid.settings.SettingsModule
import com.example.wanandroid.todo.TodoActivity
import com.example.wanandroid.todo.TodoModule
import com.example.wanandroid.web.WebActivity
import com.example.wanandroid.web.WebModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBindingModule {
    @ContributesAndroidInjector(modules = [MainModule::class])
    abstract fun contributeMainActivity(): MainActivity

    @ContributesAndroidInjector(modules = [AccountModule::class])
    abstract fun contributeAccountActivity(): AccountActivity

    @ContributesAndroidInjector(modules = [CategoryModule::class])
    abstract fun contributeCategoryActivity(): CategoryActivity

    @ActivityScoped
    @ContributesAndroidInjector(modules = [SearchModule::class])
    abstract fun contributeSearchActivity(): SearchActivity

    @ContributesAndroidInjector(modules = [WebModule::class])
    abstract fun contributeWebActivity(): WebActivity

    @ContributesAndroidInjector(modules = [HistoryModule::class])
    abstract fun contributeHistoryActivity(): HistoryActivity

    @ActivityScoped
    @ContributesAndroidInjector(modules = [MarkModule::class])
    abstract fun contributeMarkActivity(): MarkActivity

    @ActivityScoped
    @ContributesAndroidInjector(modules = [TodoModule::class])
    abstract fun contributeTodoActivity(): TodoActivity

    @ContributesAndroidInjector(modules = [SettingsModule::class])
    abstract fun contributeSettingsActivity(): SettingsActivity
}