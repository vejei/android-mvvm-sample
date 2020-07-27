package com.example.wanandroid.main.project

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.wanandroid.data.Category
import com.example.wanandroid.utils.escapeHtml

class ProjectPageAdapter(
    fragmentManager: FragmentManager
) : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    var data = mutableListOf<Category>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItem(position: Int): Fragment {
        return ProjectPageFragment.newInstance(data[position].id)
    }

    override fun getCount(): Int = data.size

    override fun getPageTitle(position: Int): CharSequence? {
        return data[position].name?.escapeHtml()
    }
}