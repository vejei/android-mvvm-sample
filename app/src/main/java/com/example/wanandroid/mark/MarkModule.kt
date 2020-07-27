package com.example.wanandroid.mark

import androidx.lifecycle.ViewModel
import com.example.wanandroid.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class MarkModule {
    @ContributesAndroidInjector
    abstract fun contributeMarkArticlesFragment(): MarkArticlesFragment

    @Binds
    @IntoMap
    @ViewModelKey(MarkViewModel::class)
    abstract fun bindMarkViewModel(viewModel: MarkViewModel): ViewModel
}