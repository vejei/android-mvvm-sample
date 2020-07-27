package com.example.wanandroid.main

import android.content.Intent
import androidx.appcompat.widget.Toolbar
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.example.wanandroid.R
import com.example.wanandroid.search.SearchActivity
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class MainActivityTest {
    private lateinit var scenario: ActivityScenario<MainActivity>

    @Before
    fun setup() {
        scenario = ActivityScenario.launch(MainActivity::class.java)
    }

    @Test
    fun fourFragmentsByDefault() {
        scenario.onActivity {
            assertThat(it.supportFragmentManager.fragments.size).isEqualTo(4)
        }.close()
    }

    @Test
    fun accountMenuItemClicked_openMineDialog() {
        onView(withId(R.id.menu_account)).perform(click())

        scenario.onActivity {
            val bottomSheetFragment = it.supportFragmentManager.findFragmentByTag(MineDialogFragment.SHOW_TAG)

            assertThat(bottomSheetFragment).isNotNull()
            assertThat(bottomSheetFragment).isInstanceOf(MineDialogFragment::class.java)
        }.close()
    }

    @Test
    fun searchMenuItemClicked_startSearchActivity() {
        onView(withId(R.id.menu_search)).perform(click())

        scenario.onActivity {
            val expectedIntent = Intent(it, SearchActivity::class.java)
            val actualIntent = shadowOf(it.application).nextStartedActivity

            assertThat(expectedIntent.component).isEqualTo(actualIntent.component)
        }.close()
    }

    @Test
    fun removeToolbarElevation_onProjectNavigationItemSelected() {
        onView(withId(R.id.menu_project)).perform(click())

        scenario.onActivity {
            val elevation = it.findViewById<Toolbar>(R.id.toolbar).elevation

            assertThat(elevation).isZero()
        }.close()
    }
}