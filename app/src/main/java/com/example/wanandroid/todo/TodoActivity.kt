package com.example.wanandroid.todo

import android.accounts.AccountManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.example.wanandroid.R
import com.example.wanandroid.common.InjectableActivity
import com.example.wanandroid.databinding.ActivityTodoBinding
import com.example.wanandroid.utils.addFragment
import com.example.wanandroid.utils.closeKeyboard
import com.example.wanandroid.utils.viewModelProvider
import javax.inject.Inject

class TodoActivity : InjectableActivity() {
    private lateinit var binding: ActivityTodoBinding
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: TodoViewModel
    @Inject lateinit var accountManager: AccountManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTodoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = viewModelProvider(viewModelFactory)

        val account = accountManager.getAccountsByType(getString(R.string.account_type)).first()
        viewModel.onAccountRead(account.name, accountManager.getPassword(account))

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(R.string.mine_dialog_todo)

        if (savedInstanceState == null) addFragment(R.id.fragment_container, TodoListFragment())

        supportFragmentManager.addOnBackStackChangedListener {
            closeKeyboard()
            val titleRes = when (supportFragmentManager.findFragmentById(R.id.fragment_container)) {
                is TodoListFragment -> R.string.mine_dialog_todo
                is TodoEditFragment -> R.string.todo_edit_new_item_toolbar_title
                else -> R.string.mine_dialog_todo
            }
            supportActionBar?.setTitle(titleRes)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.todo, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.menu_add -> {
                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(
                        R.anim.fragment_slide_in_bottom, R.anim.fragment_slide_out_bottom,
                        R.anim.fragment_slide_in_bottom, R.anim.fragment_slide_out_bottom
                    )
                    .add(R.id.fragment_container, TodoEditFragment())
                    .addToBackStack(null)
                    .commit()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}