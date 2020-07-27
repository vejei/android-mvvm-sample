package com.example.wanandroid.main.project

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.wanandroid.common.InjectableFragment
import com.example.wanandroid.data.Category
import com.example.wanandroid.data.Result
import com.example.wanandroid.databinding.FragmentProjectBinding
import com.example.wanandroid.utils.errorMessage
import com.example.wanandroid.utils.shortMessage
import com.example.wanandroid.utils.viewModelProvider
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import timber.log.Timber
import javax.inject.Inject

class ProjectFragment : InjectableFragment() {
    private lateinit var binding: FragmentProjectBinding
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: ProjectViewModel
    @Inject lateinit var disposables: CompositeDisposable

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = viewModelProvider(viewModelFactory)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProjectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val projectPageAdapter = ProjectPageAdapter(childFragmentManager)
        with(binding) {
            viewPager.adapter = projectPageAdapter
            tabLayout.setupWithViewPager(viewPager)

            viewModel.categories.subscribe {
                when(it) {
                    is Result.Error -> {
                        Timber.e(it.message)
                        errorMessage()
                    }
                    is Result.Failure -> shortMessage(it.message)
                    is Result.Success -> {
                        projectPageAdapter.data = it.data ?: emptyList<Category>().toMutableList()
                    }
                }
            }.addTo(disposables)
        }

        viewModel.fetchCategories()
    }

    override fun onDestroy() {
        disposables.dispose()
        super.onDestroy()
    }

    companion object {
        val TAG = ProjectFragment::class.java.simpleName
    }
}