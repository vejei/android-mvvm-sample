package com.example.wanandroid.todo

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.ViewModelProvider
import com.example.wanandroid.common.InjectableFragment
import com.example.wanandroid.data.Result
import com.example.wanandroid.databinding.FragmentTodoEditBinding
import com.example.wanandroid.utils.*
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import timber.log.Timber
import javax.inject.Inject

class TodoEditFragment : InjectableFragment() {
    private lateinit var binding: FragmentTodoEditBinding
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: TodoViewModel
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
        setHasOptionsMenu(true)
        binding = FragmentTodoEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            editTextNameInput.onTextChanged(viewModel::onNameChanged)
            editTextDescription.onTextChanged(viewModel::onDescriptionChanged)

            buttonSave.clicks().subscribe { viewModel.onSaveButtonClicked() }.addTo(disposables)
            buttonSave.isEnabled = false
            viewModel.submitEnabled.subscribe { buttonSave.isEnabled = it }.addTo(disposables)

            viewModel.submitResult.subscribe {
                disableForm(it)

                when (it) {
                    is Result.Failure -> shortMessage(it.message)
                    is Result.Error -> {
                        Timber.e(it.message)
                        errorMessage()
                    }
                    is Result.Success -> {
                        Timber.d("post success")
                        // 重新获取待办列表
                        popBackStack()
                        viewModel.fetchTodo()
                    }
                }
            }.addTo(disposables)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
    }

    private fun disableForm(result: Result<*>) {
        with(binding) {
            when (result) {
                is Result.Loading -> {
                    editTextNameInput.isEnabled = false
                    editTextDescription.isEnabled = false
                    buttonSave.isEnabled = false
                }
                else -> {
                    editTextNameInput.isEnabled = true
                    editTextDescription.isEnabled = true
                    buttonSave.isEnabled = true
                }
            }
        }
    }

    override fun onDestroy() {
        viewModel.onFormClose()
        super.onDestroy()
    }
}