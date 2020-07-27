package com.example.wanandroid.todo

import androidx.lifecycle.ViewModel
import com.example.wanandroid.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class TodoModule {

    @ContributesAndroidInjector
    abstract fun contributeTodoListFragment(): TodoListFragment

    @ContributesAndroidInjector
    abstract fun contributeTodoEditFragment(): TodoEditFragment

    @Binds
    @IntoMap
    @ViewModelKey(TodoViewModel::class)
    abstract fun bindTodoViewModel(viewModel: TodoViewModel): ViewModel
}