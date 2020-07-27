package com.example.wanandroid.main.category

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.wanandroid.data.Category
import timber.log.Timber

class PageAdapter(
    fragmentManager: FragmentManager
) : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    var data = mutableListOf<Category>()

    override fun getItem(position: Int): Fragment {
        Timber.d("page adapter category name: ${data[position].name}, id: ${data[position].id}")
        return CategoryPageFragment.newInstance(data[position].id)
    }

    override fun getCount(): Int {
        return data.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return data[position].name
    }
}