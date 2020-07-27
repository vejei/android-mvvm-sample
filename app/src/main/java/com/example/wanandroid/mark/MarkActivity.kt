package com.example.wanandroid.mark

import android.accounts.AccountManager
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.wanandroid.R
import com.example.wanandroid.common.InjectableActivity
import com.example.wanandroid.databinding.ActivityMarkBinding
import com.example.wanandroid.utils.setupActionBar
import com.example.wanandroid.utils.viewModelProvider
import javax.inject.Inject

class MarkActivity : InjectableActivity() {
    private lateinit var binding: ActivityMarkBinding
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: MarkViewModel
    @Inject lateinit var accountManager: AccountManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMarkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = viewModelProvider(viewModelFactory)

        setupActionBar(binding.toolbar, R.string.menu_mark, true)

        val account = accountManager.getAccountsByType(getString(R.string.account_type)).first()
        viewModel.onAccountRead(account.name, accountManager.getPassword(account))

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, MarkArticlesFragment())
                .commit()
        }
    }
}