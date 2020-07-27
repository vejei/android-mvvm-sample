package com.example.wanandroid.main.home

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wanandroid.R
import com.example.wanandroid.data.Banner
import com.example.wanandroid.databinding.ItemCarouselBinding
import com.example.wanandroid.web.WebActivity
import com.example.wanandroid.web.WebActivity.Companion.KEY_ARTICLE_TITLE
import com.example.wanandroid.web.WebActivity.Companion.KEY_ARTICLE_URL
import com.example.wanandroid.web.WebActivity.Companion.KEY_MARK_ENABLED
import io.github.vejei.carouselview.CarouselAdapter

class PageAdapter : CarouselAdapter<CarouselViewHolder>() {
    private val data = mutableListOf<Banner>()

    fun setData(data: MutableList<Banner>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreatePageViewHolder(parent: ViewGroup, viewType: Int): CarouselViewHolder {
        return CarouselViewHolder(
            ItemCarouselBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindPageViewHolder(holder: CarouselViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getPageCount(): Int {
        return data.size
    }
}

class CarouselViewHolder(val binding: ItemCarouselBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(banner: Banner) {
        with(binding) {
            imageViewPreview.clipToOutline = true
            Glide.with(itemView.context).load(banner.imagePath)
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_placeholder)
                .into(imageViewPreview)

            val context = itemView.context
            itemView.setOnClickListener {
                context.startActivity(Intent(context, WebActivity::class.java).apply {
                    putExtra(KEY_ARTICLE_TITLE, banner.title)
                    putExtra(KEY_ARTICLE_URL, banner.url)
                    putExtra(KEY_MARK_ENABLED, false)
                })
            }
        }
    }
}