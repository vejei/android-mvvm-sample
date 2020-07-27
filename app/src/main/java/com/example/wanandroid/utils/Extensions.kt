package com.example.wanandroid.utils

import android.app.Activity
import android.content.Context
import android.os.Build
import android.text.Editable
import android.text.Html
import android.text.Spanned
import android.text.TextWatcher
import android.text.format.DateUtils
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.uikit.DividerDecoration
import com.example.wanandroid.R

fun EditText.onTextChanged(input: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            input(s.toString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })
}

fun EditText.text() = this.text.toString()

fun Fragment.errorMessage(message: String? = null) {
    Toast.makeText(
        context,
        message ?: getString(R.string.error_occurred_message),
        Toast.LENGTH_SHORT
    ).show()
}

fun Fragment.shortMessage(message: String?) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun Activity.errorMessage(message: String? = null) {
    Toast.makeText(
        this,
        message ?: getString(R.string.error_occurred_message),
        Toast.LENGTH_SHORT
    ).show()
}

fun Activity.shortMessage(message: String?) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Long.formatDate(): CharSequence? {
    return DateUtils.getRelativeTimeSpanString(
        this, System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS
    )
}

inline fun <reified VM : ViewModel> FragmentActivity.viewModelProvider(
    provider: ViewModelProvider.Factory
) = ViewModelProvider(this, provider).get(VM::class.java)

inline fun <reified VM : ViewModel> Fragment.viewModelProvider(
    provider: ViewModelProvider.Factory
) = ViewModelProvider(this, provider).get(VM::class.java)

fun RecyclerView.addDivider() {
    this.addItemDecoration(DividerDecoration(context).apply {
        setDrawable(ContextCompat.getDrawable(context, R.drawable.list_divider))
    })
}

fun String.escapeHtml(): Spanned? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(this, Html.FROM_HTML_MODE_COMPACT)
    } else {
        Html.fromHtml(this)
    }
}

fun AppCompatActivity.setupActionBar(toolbar: Toolbar, title: CharSequence, showHomeAsUp: Boolean) {
    setSupportActionBar(toolbar)
    supportActionBar?.title = title
    supportActionBar?.setDisplayHomeAsUpEnabled(showHomeAsUp)
}

fun AppCompatActivity.setupActionBar(toolbar: Toolbar, titleResId: Int, showHomeAsUp: Boolean) {
    setupActionBar(toolbar, getString(titleResId), showHomeAsUp)
}

fun FragmentActivity.addFragment(containerId: Int = R.id.fragment_container, fragment: Fragment) {
    supportFragmentManager.beginTransaction()
        .add(containerId, fragment)
        .commit()
}

fun Activity.closeKeyboard() {
    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0 )
}

fun Fragment.closeKeyboard() {
    val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0 )
}

fun Fragment.popBackStack() {
    requireActivity().supportFragmentManager.popBackStack()
}