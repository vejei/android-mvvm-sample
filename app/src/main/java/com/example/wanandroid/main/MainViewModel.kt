package com.example.wanandroid.main

import androidx.lifecycle.ViewModel
import com.example.wanandroid.R
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class MainViewModel @Inject constructor() : ViewModel() {
    val hasTab = PublishSubject.create<Boolean>()

    fun onBottomNavigationItemSelected(itemId: Int) {
        val enabled = when(itemId) {
            R.id.menu_home -> false
            R.id.menu_category -> false
            R.id.menu_project -> true
            R.id.menu_square -> false
            else -> false
        }
        hasTab.onNext(enabled)
    }
}