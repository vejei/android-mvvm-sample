package com.example.wanandroid.main.project

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wanandroid.R
import com.example.wanandroid.common.*
import com.example.wanandroid.data.Article
import com.example.wanandroid.databinding.ItemProjectArticleBinding
import com.example.wanandroid.utils.Transformers.throttleClick
import com.example.wanandroid.utils.formatDate
import com.example.wanandroid.web.WebActivity
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.rxkotlin.addTo

class ArticleAdapter(retryListener: () -> Unit) : ExtraAdapter<Article>(retryListener) {
    override fun createContentViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return ArticleViewHolder(
            ItemProjectArticleBinding.inflate(LayoutInflater.from(parent.context), parent,
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
                    putExtra(WebActivity.KEY_ARTICLE_AUTHOR_NAME, article.author)
                    putExtra(WebActivity.KEY_ARTICLE_ID, article.id)
                    putExtra(WebActivity.KEY_ARTICLE_PUB_DATE, article.publishTime)
                })*/
                WebActivity.start(context, article.id, article.title, article.link, article.author,
                    article.publishTime)
            }.addTo(disposables)
        }
    }
}

class ArticleViewHolder(val binding: ItemProjectArticleBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(article: Article) {
        with(binding) {
            textViewUsername.text = article.author
            textViewPublishDate.text = article.publishTime.formatDate()

            imageViewPreview.clipToOutline = true
            Glide.with(itemView).load(article.envelopePic)
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_placeholder)
                .into(imageViewPreview) // 预览图

            textViewTitle.text = article.title

            if (article.description?.isNotBlank() == true) {
                textViewDescription.visibility = View.VISIBLE
                textViewDescription.text = article.description
            } else {
                textViewDescription.visibility = View.GONE
            }

            textViewThumbUpCount.text = article.zan.toString()
            textViewViewCount.text = article.visible.toString()
        }
    }
}