package com.example.wanandroid.main

import androidx.lifecycle.ViewModel
import com.example.wanandroid.di.ViewModelKey
import com.example.wanandroid.main.category.CategoryFragment
import com.example.wanandroid.main.category.CategoryViewModel
import com.example.wanandroid.main.home.HomeFragment
import com.example.wanandroid.main.home.HomeViewModel
import com.example.wanandroid.main.project.ProjectFragment
import com.example.wanandroid.main.project.ProjectModule
import com.example.wanandroid.main.project.ProjectViewModel
import com.example.wanandroid.main.square.SquareFragment
import com.example.wanandroid.main.square.SquareViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class MainModule {
    @ContributesAndroidInjector
    abstract fun contributeMainBottomSheetDialogFragment(): MineDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeSignOutDialogFragment(): SignOutDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeHomeFragment(): HomeFragment

    @ContributesAndroidInjector
    abstract fun contributeCategoryFragment(): CategoryFragment

    @ContributesAndroidInjector(modules = [ProjectModule::class])
    abstract fun contributeProjectFragment(): ProjectFragment

    @ContributesAndroidInjector
    abstract fun contributeSquareFragment(): SquareFragment

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(viewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun bindHomeViewModel(viewModel: HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CategoryViewModel::class)
    abstract fun bindCategoryViewModel(viewModel: CategoryViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ProjectViewModel::class)
    abstract fun bindProjectViewModel(viewModel: ProjectViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SquareViewModel::class)
    abstract fun bindSquareViewModel(viewModel: SquareViewModel): ViewModel
}