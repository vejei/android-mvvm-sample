package com.example.wanandroid.web

import android.Manifest
import android.accounts.Account
import android.annotation.SuppressLint
import android.content.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.*
import android.webkit.*
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.wanandroid.R
import com.example.wanandroid.common.AuthenticationActivity
import com.example.wanandroid.data.Result
import com.example.wanandroid.databinding.ActivityWebBinding
import com.example.wanandroid.utils.errorMessage
import com.example.wanandroid.utils.shortMessage
import com.example.wanandroid.utils.viewModelProvider
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber
import javax.inject.Inject

class WebActivity : AuthenticationActivity() {
    private lateinit var binding: ActivityWebBinding
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: WebViewModel
    @Inject lateinit var disposables: CompositeDisposable
    @Inject lateinit var preferences: SharedPreferences

    private var articleId: Int = -1
    private var articleTitle: String? = null
    private var articleUrl: String? = null
    private var articleAuthorName: String? = null
    private var publishDate: Long = 0
    private var markEnabled: Boolean = true

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = viewModelProvider(viewModelFactory)

        articleId = intent.getIntExtra(KEY_ARTICLE_ID, -1)
        articleTitle = intent.getStringExtra(KEY_ARTICLE_TITLE)
        articleUrl = intent.getStringExtra(KEY_ARTICLE_URL)
        articleAuthorName = intent.getStringExtra(KEY_ARTICLE_AUTHOR_NAME)
        publishDate = intent.getLongExtra(KEY_ARTICLE_PUB_DATE, 0)
        markEnabled = intent.getBooleanExtra(KEY_MARK_ENABLED, true)

        Timber.d("title: $articleTitle, url: $articleUrl, author name: $articleAuthorName")
        val saveHistory = preferences.getBoolean(getString(R.string.settings_enable_history_key), true)
        viewModel.onArticleOpen(saveHistory, articleId, articleTitle, articleUrl, articleAuthorName, publishDate)

        with(binding) {
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayShowTitleEnabled(false)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            textViewToolbarTitle.text = articleTitle
            textViewToolbarTitle.isSelected = true

            webView.settings.javaScriptEnabled = true
            webView.settings.useWideViewPort = true
            webView.settings.loadWithOverviewMode = true

            webView.webViewClient = object : WebViewClient() {
                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
                    progressBarWebLoading.visibility = View.GONE
                }

                /*override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    val intent = Intent().apply {
                        action = Intent.ACTION_VIEW
                        data = request?.url
                    }
                    startActivity(intent)
                    return true
                }*/
            }
            webView.webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    if (newProgress == 0) {
                        Timber.d("loading start.")
                        progressBarWebLoading.visibility = View.VISIBLE
                    } else if (newProgress == 100) {
                        Timber.d("loading end.")
                        progressBarWebLoading.visibility = View.GONE
                    }
                    progressBarWebLoading.progress = newProgress
                }

                override fun onReceivedTitle(view: WebView?, title: String?) {
                    super.onReceivedTitle(view, title)
                    Timber.d("onReceivedTitle, title: $title")
                    textViewToolbarTitle.text = title
                }
            }
            webView.setOnKeyListener(object : View.OnKeyListener {
                override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                    if (keyCode == KeyEvent.KEYCODE_BACK && event?.action == MotionEvent.ACTION_UP
                        && webView.canGoBack()) {
                        webView.goBack()
                        return true
                    }
                    return false
                }
            })

            viewModel.markResult.subscribe {
                when(it) {
                    is Result.Loading -> shortMessage("收藏中")
                    is Result.Failure -> shortMessage(it.message)
                    is Result.Error -> {
                        Timber.e(it.message)
                        errorMessage()
                    }
                    is Result.Success -> shortMessage("已收藏")
                }
            }.addTo(disposables)

            webView.loadUrl(articleUrl)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.web, menu)
        menu?.findItem(R.id.menu_mark)?.isVisible = markEnabled
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_mark -> {
                // 收藏
                markArticle()
            }
            R.id.menu_share -> {
                // 分享
                shareArticle()
            }
            R.id.menu_reload -> {
                // 重新加载
                binding.webView.reload()
            }
            R.id.menu_copy_url -> {
                // 复制链接
                copyArticleUrl()
            }
            R.id.menu_open_in_browser -> {
                // 在浏览器打开链接
                openInBrowser()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == GET_ACCOUNTS) {
            shortMessage(getString(R.string.get_accounts_permission_denied_toast_message))
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        // 获取账号信息（密码和用户名，用于构造头部），然后收藏文章
        if (requestCode == GET_ACCOUNTS) {
            requestAccount()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    private fun markArticle() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (EasyPermissions.hasPermissions(this, Manifest.permission.GET_ACCOUNTS)) {
                requestAccount()
            } else {
                EasyPermissions.requestPermissions(
                    this, getString(R.string.get_accounts_permission_request_rationale_message),
                    GET_ACCOUNTS, Manifest.permission.GET_ACCOUNTS
                )
            }
        } else {
            requestAccount()
        }
    }

    override fun onAuthenticated(account: Account) {
        Timber.d("has account, get account info")
        viewModel.onAccountRead(account.name, accountManager.getPassword(account))
        viewModel.onMarkTrigger()
    }

    private fun shareArticle() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "【${articleTitle}】：$articleUrl （玩Android）")
        }
        startActivity(intent)
    }

    private fun copyArticleUrl() {
        val clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        clipboardManager.setPrimaryClip(ClipData.newPlainText("article_url", articleUrl))
        Toast.makeText(this, R.string.url_copied_toast_message, Toast.LENGTH_SHORT).show()
    }

    private fun openInBrowser() {
        val intent = Intent().apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse(articleUrl)
        }
        startActivity(intent)
    }

    companion object {
        const val KEY_ARTICLE_ID = "article_id"
        const val KEY_ARTICLE_TITLE = "article_title"
        const val KEY_ARTICLE_URL = "article_url"
        const val KEY_ARTICLE_AUTHOR_NAME = "article_author_name"
        const val KEY_ARTICLE_PUB_DATE = "article_pub_date"
        const val KEY_MARK_ENABLED = "mark_enabled"

        const val GET_ACCOUNTS = 1

        @JvmStatic
        fun start(context: Context, articleId: Int, title: String?, url: String?, authorName: String?,
                  publishDate: Long, markEnabled: Boolean? = true) {
            context.startActivity(Intent(context, WebActivity::class.java).apply {
                putExtra(KEY_ARTICLE_ID, articleId)
                putExtra(KEY_ARTICLE_TITLE, title)
                putExtra(KEY_ARTICLE_URL, url)
                putExtra(KEY_ARTICLE_AUTHOR_NAME, authorName)
                putExtra(KEY_ARTICLE_PUB_DATE, publishDate)
                putExtra(KEY_MARK_ENABLED, markEnabled)
            })
        }
    }
}