package com.example.wanandroid.account

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.wanandroid.R
import com.example.wanandroid.data.Result
import com.example.wanandroid.databinding.FragmentSignUpBinding
import com.example.wanandroid.utils.*
import com.example.wanandroid.utils.Transformers.throttleClick
import com.jakewharton.rxbinding3.view.clicks
import dagger.android.support.DaggerFragment
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import timber.log.Timber
import javax.inject.Inject

class SignUpFragment : DaggerFragment() {
    private lateinit var binding: FragmentSignUpBinding
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var viewModel: SignUpViewModel
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
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            editTextUsername.onTextChanged { viewModel.onUsernameChanged(it) }
            editTextPassword.onTextChanged { viewModel.onPasswordChanged(it) }
            editTextPasswordRepeat.onTextChanged { viewModel.onPasswordRepeated(it) }

            buttonSubmit.clicks().compose(throttleClick()).subscribe {
                viewModel.onSubmitButtonClicked()
                setFormStatus(true)
            }.addTo(disposables)

            textViewSignInHint.clicks().compose(throttleClick())
                .subscribe { (activity as AccountActivity).toSignIn() }
                .addTo(disposables)

            viewModel.passwordConsistency.subscribe {
                textInputLayoutPasswordRepeat.isErrorEnabled = !it
                if (!it) {
                    textInputLayoutPasswordRepeat.error =
                        getString(R.string.sign_up_password_not_match_error_message)
                }
            }.addTo(disposables)

            viewModel.submitEnabled.subscribe { buttonSubmit.isEnabled = it }
                .addTo(disposables)

            viewModel.userData.subscribe {
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
        }
    }

    private fun setFormStatus(processing: Boolean) {
        with(binding) {
            buttonSubmit.isProcessing = processing
            editTextUsername.isEnabled = !processing
            editTextPassword.isEnabled = !processing
            editTextPasswordRepeat.isEnabled = !processing
        }
    }
}