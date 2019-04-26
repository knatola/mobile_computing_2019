package com.example.whatisup.activity

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.example.whatisup.BuildConfig
import com.example.whatisup.R
import com.example.whatisup.src.ui.MainActivity
import kotlinx.android.synthetic.main.activity_main.view.*
import org.junit.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Rule
    @JvmField
    val mainActivity: ActivityTestRule<MainActivity> = ActivityTestRule<MainActivity>(MainActivity::class.java)

    @Before
    fun setUp() {
        object {
            init {
                BuildConfig.IS_TESTING.set(true)
            }
        }
    }

    @After
    fun tearDown() {
        object {
            init {
                BuildConfig.IS_TESTING.set(false)
            }
        }
    }

    @Test
    fun testNavigation() {
        // the whole buildconfig flag might be a bit hacky, there might a better way to check if a test is running at runtime
        Assert.assertTrue(BuildConfig.IS_TESTING.get())

        // Assert navigation is displayed
        onView(withId(R.id.custom_nav)).check(matches(isDisplayed()))

        // Go to week view and assert content is displayed (only header checked atm)
        onView(withId(R.id.week_action)).perform(click())
        onView(withId(R.id.week_header_row)).check(matches(isDisplayed()))

        // Go to reminder view and assert content is displayed
        onView(withId(R.id.reminder_action)).perform(click())
        onView(withId(R.id.reminder_recycler_view)).check(matches(isDisplayed()))

        // Go back to day view and assert content is displayed
        onView(withId(R.id.today_action)).perform(click())
        onView(withId(R.id.image_wrapper)).check(matches(isDisplayed()))
    }
}