package com.example.wanandroid.main.square

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wanandroid.common.*
import com.example.wanandroid.data.Article
import com.example.wanandroid.databinding.ItemSquareArticleBinding
import com.example.wanandroid.utils.Transformers.throttleClick
import com.example.wanandroid.utils.escapeHtml
import com.example.wanandroid.utils.formatDate
import com.example.wanandroid.web.WebActivity
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.rxkotlin.addTo

class ArticleAdapter(retryListener: () -> Unit) : ExtraAdapter<Article>(retryListener) {
    override fun createContentViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return ArticleViewHolder(
            ItemSquareArticleBinding.inflate(LayoutInflater.from(parent.context), parent,
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
                    putExtra(WebActivity.KEY_ARTICLE_TITLE, article.title)
                    putExtra(WebActivity.KEY_ARTICLE_URL, article.link)
                    putExtra(WebActivity.KEY_ARTICLE_AUTHOR_NAME, article.shareUser)
                    putExtra(WebActivity.KEY_ARTICLE_ID, article.id)
                    putExtra(WebActivity.KEY_ARTICLE_PUB_DATE, article.publishTime)
                })*/
                WebActivity.start(context, article.id, article.title, article.link, article.shareUser,
                    article.publishTime)
            }.addTo(disposables)
        }
    }
}

class ArticleViewHolder(val binding: ItemSquareArticleBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(article: Article) {
        with(binding) {
            textViewUsername.text = article.shareUser
            textViewPublishDate.text = article.publishTime.formatDate()
            textViewTitle.text = article.title?.escapeHtml()
        }
    }
}