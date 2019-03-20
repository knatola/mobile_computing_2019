package com.example.whatisup.src.ui

import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.widget.TextView
import com.example.whatisup.R
import com.example.whatisup.src.data.ActivityProvider
import com.example.whatisup.src.utils.TimeUtils
import com.example.whatisup.src.utils.stringDate
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.custom_bottom_navigation.*

private const val TAG = "MainActivity"
const val DAY_FRAGMENT_TAG = "day"
const val SECOND_DAY_FRAG_TAG = "second"
const val WEEK_FRAGMENT_TAG = "week"
const val REMINDER_FRAGMENT_TAG = "reminder"

class MainActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var dayFragment: DayFragment
    private lateinit var weekFragment: WeekFragment
    private lateinit var reminderFragment: ReminderFragment
    private lateinit var activeFragment: Fragment
    private lateinit var bottomNav: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dayFragment = DayFragment()
        weekFragment = WeekFragment()
        reminderFragment = ReminderFragment()
        activeFragment = dayFragment
        initFragment(weekFragment, WEEK_FRAGMENT_TAG)
        initFragment(reminderFragment, REMINDER_FRAGMENT_TAG)
        supportFragmentManager.beginTransaction().add(R.id.main_framelayout, dayFragment, DAY_FRAGMENT_TAG).commit()
        bottomNav = findViewById(R.id.custom_nav)
        animateNavigation(R.id.action_today)

        toolbar = findViewById(R.id.toolbar)
        toolbar.title = "Today"
        toolbar.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        toolbar.setTitleTextColor(this.getColor(R.color.black))

        ActivityProvider.requestUpdates(this)

        today_action.setOnClickListener {
            loadFragment(dayFragment, stringDate(TimeUtils.getTodayLong()))
            animateNavigation(R.id.action_today)
        }

        week_action.setOnClickListener {
            loadFragment(weekFragment, "Week")
            animateNavigation(R.id.action_week)
        }

        reminder_action.setOnClickListener {
            loadFragment(reminderFragment, "Reminders")
            animateNavigation(R.id.action_all)
        }
    }

    private fun loadFragment(fragment: Fragment, title: String) {

        if (supportFragmentManager.findFragmentByTag(SECOND_DAY_FRAG_TAG) != null) {
            val secondDayFragment = supportFragmentManager.findFragmentByTag(SECOND_DAY_FRAG_TAG)
            supportFragmentManager.beginTransaction().hide(secondDayFragment!!).commit()
        }

        toolbar.title = title
        supportFragmentManager.beginTransaction().hide(activeFragment).show(fragment).commit()
        activeFragment = fragment
    }

    private fun initFragment(fragment: Fragment, i: String) {
        supportFragmentManager.beginTransaction().add(R.id.main_framelayout, fragment, i).hide(fragment).commit()
    }

    fun setTitle(title: String) {
        toolbar.title = title
    }

    private fun animateNavigation(id: Int) {
        val transition = AutoTransition()
        transition.duration = 100
        TransitionManager.beginDelayedTransition(bottomNav, transition)
        val cs = ConstraintSet()

        cs.clone(today_action)

        if (id == R.id.action_today) {
            DrawableCompat.setTint(select_today.drawable, ContextCompat.getColor(this, R.color.colorPrimary))
            today_text.setTextColor(getColor(R.color.colorPrimary))
            cs.setVisibility(R.id.today_selector, ConstraintSet.VISIBLE)
        } else {
            DrawableCompat.setTint(select_today.drawable, ContextCompat.getColor(this, R.color.colorAccent))
            today_text.setTextColor(getColor(R.color.colorAccent))
            cs.setVisibility(R.id.today_selector, ConstraintSet.GONE)
        }

        cs.applyTo(today_action)

        cs.clone(week_action)

        if (id == R.id.action_week) {
            cs.setVisibility(R.id.week_selector, ConstraintSet.VISIBLE)
            DrawableCompat.setTint(week_icon.drawable, ContextCompat.getColor(this, R.color.colorPrimary))
            week_text.setTextColor(getColor(R.color.colorPrimary))
        } else {
            DrawableCompat.setTint(week_icon.drawable, ContextCompat.getColor(this, R.color.colorAccent))
            week_text.setTextColor(getColor(R.color.colorAccent))
            cs.setVisibility(R.id.week_selector, ConstraintSet.GONE)
        }
        cs.applyTo(week_action)

        cs.clone(reminder_action)

        if (id == R.id.action_all) {
            DrawableCompat.setTint(reminder_icon.drawable, ContextCompat.getColor(this, R.color.colorPrimary))
            reminder_text.setTextColor(getColor(R.color.colorPrimary))
            cs.setVisibility(R.id.reminder_selector, ConstraintSet.VISIBLE)
        } else {
            reminder_text.setTextColor(getColor(R.color.colorAccent))
            DrawableCompat.setTint(reminder_icon.drawable, ContextCompat.getColor(this, R.color.colorAccent))
            cs.setVisibility(R.id.reminder_selector, ConstraintSet.GONE)
        }

        cs.applyTo(reminder_action)

        }
}
