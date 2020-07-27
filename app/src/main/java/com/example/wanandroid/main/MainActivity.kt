package com.example.wanandroid.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.wanandroid.R
import com.example.wanandroid.common.InjectableActivity
import com.example.wanandroid.main.category.CategoryFragment
import com.example.wanandroid.databinding.ActivityMainBinding
import com.example.wanandroid.main.home.HomeFragment
import com.example.wanandroid.main.project.ProjectFragment
import com.example.wanandroid.search.SearchActivity
import com.example.wanandroid.main.square.SquareFragment
import com.example.wanandroid.utils.setupActionBar
import com.example.wanandroid.utils.viewModelProvider
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import javax.inject.Inject

class MainActivity : InjectableActivity() {
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: MainViewModel
    @Inject lateinit var disposables: CompositeDisposable
    private lateinit var binding: ActivityMainBinding
    private lateinit var activeFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = viewModelProvider(viewModelFactory)

        setupActionBar(binding.toolbar, R.string.menu_home, false)

        addFragments()
        setupBottomNavigationView()

        viewModel.hasTab.subscribe { this.setToolbarElevation(it) }.addTo(disposables)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_account -> {
                // 打开底部对话框
                MineDialogFragment().show(supportFragmentManager, MineDialogFragment.SHOW_TAG)
            }
            R.id.menu_search -> {
                // 跳转到搜索
                startActivity(Intent(this, SearchActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun addFragments() {
        val categoryFragment = CategoryFragment()
        addAndHideFragment(categoryFragment, CategoryFragment.TAG)

        val projectFragment = ProjectFragment()
        addAndHideFragment(projectFragment, ProjectFragment.TAG)

        val squareFragment = SquareFragment()
        addAndHideFragment(squareFragment, SquareFragment.TAG)

        val homeFragment = HomeFragment()
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, homeFragment, HomeFragment.TAG)
            .commit()
        activeFragment = homeFragment
    }

    private fun addAndHideFragment(fragment: Fragment, tag: String? = null) {
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, fragment, tag)
            .hide(fragment)
            .commit()
    }

    private fun setupBottomNavigationView() {
        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            viewModel.onBottomNavigationItemSelected(it.itemId)
            val selectedFragment = when(it.itemId) {
                R.id.menu_home -> {
                    supportActionBar?.setTitle(R.string.menu_home)
                    supportFragmentManager.findFragmentByTag(HomeFragment.TAG)
                }
                R.id.menu_category -> {
                    supportActionBar?.setTitle(R.string.menu_category)
                    supportFragmentManager.findFragmentByTag(CategoryFragment.TAG)
                }
                R.id.menu_project -> {
                    supportActionBar?.setTitle(R.string.menu_project)
                    supportFragmentManager.findFragmentByTag(ProjectFragment.TAG)
                }
                R.id.menu_square -> {
                    supportActionBar?.setTitle(R.string.menu_square)
                    supportFragmentManager.findFragmentByTag(SquareFragment.TAG)
                }
                else -> supportFragmentManager.findFragmentByTag(HomeFragment.TAG)
            }
            if (selectedFragment != activeFragment && selectedFragment != null) {
                supportFragmentManager.beginTransaction()
                    .show(selectedFragment).hide(activeFragment)
                    .commit()
                activeFragment = selectedFragment
            }
            true
        }
    }

    private fun setToolbarElevation(hasTab: Boolean) {
        val elevation = if (hasTab) {
            0f
        } else {
            resources.getDimension(R.dimen.elevation)
        }
        binding.toolbar.elevation = elevation
    }
}
