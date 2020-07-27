package com.example.wanandroid.search

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.ViewModelProvider
import com.example.wanandroid.R
import com.example.wanandroid.common.InjectableActivity
import com.example.wanandroid.databinding.ActivitySearchBinding
import com.example.wanandroid.utils.onTextChanged
import com.example.wanandroid.utils.viewModelProvider
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import timber.log.Timber
import javax.inject.Inject

class SearchActivity : InjectableActivity() {
    private lateinit var binding: ActivitySearchBinding
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: SearchViewModel
    @Inject lateinit var disposables: CompositeDisposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = viewModelProvider(viewModelFactory)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        if (savedInstanceState == null) addFragments()

        binding.editTextKeyword.onTextChanged { viewModel.onSearchKeywordChanged(it) }
        binding.editTextKeyword.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                Timber.d("keyboard search button clicked.")
                viewModel.onSearchTrigger() // 点击键盘上的搜索按钮时触发搜索
                true
            } else {
                false
            }
        }

        // 开始搜索时关闭键盘，并且切换 fragment
        viewModel.searchStartEvent.subscribe {
            closeKeyBoard()

            val resultFragment = supportFragmentManager.findFragmentByTag(SearchResultFragment.TAG)
            val suggestionFragment = supportFragmentManager.findFragmentByTag(SearchSuggestionFragment.TAG)
            if (resultFragment != null && suggestionFragment != null) {
                supportFragmentManager.beginTransaction()
                    .show(resultFragment)
                    .hide(suggestionFragment)
                    .commit()
            }
        }.addTo(disposables)

        // 如果输入的关键词是空的切换回搜索建议 Fragment
        viewModel.isKeywordEmpty.subscribe {
            val resultFragment = supportFragmentManager.findFragmentByTag(SearchResultFragment.TAG)
            val suggestionFragment = supportFragmentManager.findFragmentByTag(SearchSuggestionFragment.TAG)
            if (resultFragment != null && suggestionFragment != null) {
                supportFragmentManager.beginTransaction()
                    .show(suggestionFragment)
                    .hide(resultFragment)
                    .commit()
            }
        }.addTo(disposables)

        // 搜索热词被点击，更新输入框内容并移动光标到搜索词尾部
        viewModel.suggestionItemClickEvent.subscribe {
            binding.editTextKeyword.setText(it)
            binding.editTextKeyword.setSelection(it.length)
            viewModel.onSearchTrigger()
        }.addTo(disposables)
    }

    private fun addFragments() {
        val suggestionFragment = SearchSuggestionFragment()
        val resultFragment = SearchResultFragment()
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, suggestionFragment, SearchSuggestionFragment.TAG)
            .add(R.id.fragment_container, resultFragment, SearchResultFragment.TAG)
            .hide(resultFragment)
            .show(suggestionFragment)
            .commit()
    }

    private fun closeKeyBoard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0 )
    }
}