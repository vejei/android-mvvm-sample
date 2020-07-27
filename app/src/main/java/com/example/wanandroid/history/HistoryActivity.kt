package com.example.wanandroid.history

import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import com.example.wanandroid.R
import com.example.wanandroid.databinding.ActivityHistoryBinding
import com.example.wanandroid.settings.HistoryDisableAlertDialogFragment
import com.example.wanandroid.utils.viewModelProvider
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class HistoryActivity : DaggerAppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: HistoryViewModel
    @Inject lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = viewModelProvider(viewModelFactory)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(R.string.mine_dialog_browsing_history)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, HistoryFragment())
                .commit()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.history, menu)
        setMenuItems(menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            /*R.id.menu_disable_history -> {
                HistoryDisableAlertDialogFragment().show(supportFragmentManager,
                    "history_disable_dialog")
                true
            }*/
            R.id.menu_clear_histories -> {
                // 清空浏览历史
                viewModel.onHistoriesClear()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        setMenuItems(menu)
        return super.onPrepareOptionsMenu(menu)
    }

    private fun setMenuItems(menu: Menu?) {
        val isEnabled = preferences.getBoolean(getString(R.string.settings_enable_history_key),
            false)
        menu?.findItem(R.id.menu_disable_history)?.isEnabled = isEnabled
        menu?.findItem(R.id.menu_clear_histories)?.isEnabled = isEnabled
    }
}