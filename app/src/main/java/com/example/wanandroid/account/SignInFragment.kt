package com.example.wanandroid.account

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.wanandroid.data.Result
import com.example.wanandroid.databinding.FragmentSignInBinding
import com.example.wanandroid.testing.TestingOpen
import com.example.wanandroid.utils.*
import com.example.wanandroid.utils.Transformers.throttleClick
import com.jakewharton.rxbinding3.view.clicks
import dagger.android.support.DaggerFragment
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import timber.log.Timber
import javax.inject.Inject

@TestingOpen
class SignInFragment : DaggerFragment() {
    private lateinit var binding: FragmentSignInBinding
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var viewModel: SignInViewModel
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
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            editTextUsername.onTextChanged { viewModel.onUsernameChanged(editTextUsername.text()) }
            editTextPassword.onTextChanged { viewModel.onPasswordChanged(editTextPassword.text()) }

            buttonSubmit.clicks().compose(throttleClick())
                .subscribe {
                    viewModel.onSubmitButtonClicked()
                    setFormStatus(true)
                }.addTo(disposables)

            viewModel.submitEnabled.subscribe { buttonSubmit.isEnabled = it ?: true }
                .addTo(disposables)

            viewModel.userData.subscribe {
                Timber.d("userData subscribe")
                when (it) {
                    is Result.Success -> {
                        Timber.d("success")
                        (activity as AccountActivity).apply {
                            setAccountAuthenticatorResult(addAccount(it.data, editTextPassword.text()))
                            finish()
                        }
                    }
                    is Result.Failure -> shortMessage(it.message)
                    is Result.Error -> {
                        Timber.e("error occurred, message: ${it.message}")
                        errorMessage()
                        setFormStatus(false)
                    }
                }

                setFormStatus(false)
            }.addTo(disposables)

            textViewSignUpHint.clicks().compose(throttleClick())
                .subscribe { (activity as AccountActivity).toSignUp() }
                .addTo(disposables)
        }
    }

    private fun setFormStatus(processing: Boolean) {
        with(binding) {
            buttonSubmit.isProcessing = processing
            editTextUsername.isEnabled = !processing
            editTextPassword.isEnabled = !processing
        }
    }

    override fun onDestroy() {
        disposables.dispose()
        super.onDestroy()
    }
}