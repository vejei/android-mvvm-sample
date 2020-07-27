package com.example.wanandroid.main.category

import android.os.Bundle
import com.example.wanandroid.data.Category
import com.example.wanandroid.databinding.ActivityCategoryBinding
import com.google.gson.Gson
import dagger.android.support.DaggerAppCompatActivity

class CategoryActivity : DaggerAppCompatActivity() {
    private lateinit var binding: ActivityCategoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val categoryJson = intent.getStringExtra(KEY_CATEGORY_JSON)
        val category = Gson().fromJson<Category>(categoryJson, Category::class.java)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.textViewToolbarTitle.text = category.name

        with(binding) {
            val childCategories = category.children ?: return@with
            viewPager.adapter = PageAdapter(supportFragmentManager).apply {
                data = childCategories
            }
            tabLayout.setupWithViewPager(viewPager)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    companion object {
        const val KEY_CATEGORY_JSON = "category_json"
    }
}