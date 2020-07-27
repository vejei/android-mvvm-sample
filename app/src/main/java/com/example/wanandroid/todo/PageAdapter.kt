package com.example.wanandroid.todo

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.wanandroid.R

class PageAdapter(
    private val context: Context, fragmentManager: FragmentManager
) : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    var titleList = mutableListOf<String>(
        context.getString(R.string.todo_uncompleted_tab_title),
        context.getString(R.string.todo_completed_tab_title)
    )

    override fun getItem(position: Int): Fragment {
        return when(position) {
            0 -> TodoListFragment.newInstance(0)
            1 -> TodoListFragment.newInstance(1)
            else -> TodoListFragment.newInstance(0)
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return titleList[position]
    }
}