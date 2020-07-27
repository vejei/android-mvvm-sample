package com.example.wanandroid.main.category

import androidx.lifecycle.ViewModel
import com.example.wanandroid.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class CategoryModule {

    @ContributesAndroidInjector
    abstract fun contributeCategoryPageFragment(): CategoryPageFragment

    @Binds
    @IntoMap
    @ViewModelKey(CategoryViewModel::class)
    abstract fun bindCategoryViewModel(viewModel: CategoryViewModel): ViewModel
}