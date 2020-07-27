package com.example.wanandroid.main.home

import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wanandroid.common.ExtraAdapter
import com.example.wanandroid.data.Article
import com.example.wanandroid.databinding.ItemHomeArticleBinding
import com.example.wanandroid.utils.Transformers.throttleClick
import com.example.wanandroid.utils.formatDate
import com.example.wanandroid.web.WebActivity
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.rxkotlin.addTo

class ArticleAdapter(retryListener: () -> Unit) : ExtraAdapter<Article>(retryListener) {
    override fun createContentViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return ArticleViewHolder(
            ItemHomeArticleBinding.inflate(LayoutInflater.from(parent.context), parent,
                false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ArticleViewHolder) {
            val article = getItem(position)
            holder.bind(article)

            holder.itemView.clicks().compose(throttleClick()).subscribe {
                val context = holder.itemView.context
                /*context.startActivity(Intent(context, WebActivity::class.java).apply {
                    putExtra(KEY_ARTICLE_TITLE, article.title)
                    putExtra(KEY_ARTICLE_URL, article.link)
                    putExtra(
                        KEY_ARTICLE_AUTHOR_NAME,
                        if (article.author.isNullOrBlank()) article.shareUser else article.author
                    )
                    putExtra(KEY_ARTICLE_ID, article.id)
                    putExtra(KEY_ARTICLE_PUB_DATE, article.publishTime)
                })*/
                val authorName = if (article.author.isNullOrBlank()) article.shareUser else article.author
                WebActivity.start(context, article.id, article.title, article.link, authorName,
                    article.publishTime)
            }.addTo(disposables)
        }
    }
}

class ArticleViewHolder(val binding: ItemHomeArticleBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(article: Article) {
        // 作者名， author 字段不为空使用 author 字段，否则使用 shareUser 字段
        val authorName = if (article.author.isNullOrBlank()) {
            article.shareUser
        } else {
            article.author
        }
        with(binding) {
            textViewUsername.text = authorName

            // 将返回后的时间戳格式后再显示
            textViewPublishDate.text = article.publishTime.formatDate()

            // 返回的标题里面带有转义字符，处理后再显示
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                textViewTitle.text = Html.fromHtml(article.title, Html.FROM_HTML_MODE_COMPACT)
            } else {
                textViewTitle.text = Html.fromHtml(article.title)
            }

            // 有时候描述是空的，空的时候不显示描述
            if (article.description.isNullOrBlank()) {
                textViewDescription.visibility = View.GONE
            } else {
                textViewDescription.visibility = View.VISIBLE

                // 返回的描述里面带有转义字符，处理后再显示
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    textViewDescription.text = Html.fromHtml(article.description,
                        Html.FROM_HTML_MODE_COMPACT)
                } else {
                    textViewDescription.text = Html.fromHtml(article.description)
                }
            }

            // 拼接分类字符串，包含父分类和子分类
            val categoryText = "${article.superChapterName} / ${article.chapterName}"
            textViewCategory.text = categoryText
        }
    }
}