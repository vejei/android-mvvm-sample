package com.example.wanandroid.search

import androidx.lifecycle.ViewModel
import com.example.wanandroid.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class SearchModule {
    @ContributesAndroidInjector
    abstract fun contributeSuggestionFragment(): SearchSuggestionFragment

    @ContributesAndroidInjector
    abstract fun contributeResultFragment(): SearchResultFragment

    @Binds
    @IntoMap
    @ViewModelKey(SearchViewModel::class)
    abstract fun bindSearchViewModel(viewModel: SearchViewModel): ViewModel
}