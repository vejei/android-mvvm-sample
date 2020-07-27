package com.example.wanandroid.main

import com.example.wanandroid.R
import org.junit.Before
import org.junit.Test

class MainViewModelTest {
    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        viewModel = MainViewModel()
    }

    @Test
    fun projectNavigationItemSelected_hasTab() {
        val hasTab = viewModel.hasTab.test()

        viewModel.onBottomNavigationItemSelected(R.id.menu_project)

        hasTab.assertValue(true)
    }

    @Test
    fun homeNavigationItemSelected_noTab() {
        val hasTab = viewModel.hasTab.test()

        viewModel.onBottomNavigationItemSelected(R.id.menu_home)

        hasTab.assertValue(false)
    }

    @Test
    fun categoryNavigationItemSelected_noTab() {
        val hasTab = viewModel.hasTab.test()

        viewModel.onBottomNavigationItemSelected(R.id.menu_category)

        hasTab.assertValue(false)
    }

    @Test
    fun squareNavigationItemSelected_noTab() {
        val hasTab = viewModel.hasTab.test()

        viewModel.onBottomNavigationItemSelected(R.id.menu_square)

        hasTab.assertValue(false)
    }
}