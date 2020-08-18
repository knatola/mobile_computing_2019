package com.example.whatisup.src.ui

import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import com.example.whatisup.BuildConfig
import com.example.whatisup.R
import com.example.whatisup.src.BaseApp
import com.example.whatisup.src.data.ActivityProvider
import com.example.whatisup.src.utils.TimeUtils
import com.example.whatisup.src.utils.stringDate
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.Observable
import kotlinx.android.synthetic.main.custom_bottom_navigation.*
import java.util.concurrent.TimeUnit

private const val TAG = "MainActivity"

const val DAY_FRAGMENT_TAG = "day"
const val SECOND_DAY_FRAG_TAG = "second"
const val WEEK_FRAGMENT_TAG = "week"
const val REMINDER_FRAGMENT_TAG = "reminder"

@AndroidEntryPoint
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
        supportFragmentManager.beginTransaction()
            .add(R.id.main_framelayout, dayFragment, DAY_FRAGMENT_TAG).commit()
        bottomNav = findViewById(R.id.custom_nav)

        toolbar = findViewById(R.id.toolbar)
        toolbar.title = "Today"
        toolbar.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        toolbar.setTitleTextColor(this.getColor(R.color.black))

        ActivityProvider.requestUpdates(this)

        today_action.setOnClickListener {
            loadFragment(dayFragment, stringDate(TimeUtils.getTodayLong()))
        }

        week_action.setOnClickListener {
            loadFragment(weekFragment, getString(R.string.week))
        }

        reminder_action.setOnClickListener {
            loadFragment(reminderFragment, getString(R.string.reminders))
        }

        if (BuildConfig.DEBUG) {
            // just for test/demo purposes show hardcoded notification
            Observable.interval(20, TimeUnit.SECONDS)
                .subscribe { BaseApp.notificationProvider.getNotification(this, "still") }
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
        animateNavigation(fragment)
    }

    private fun initFragment(fragment: Fragment, i: String) {
        supportFragmentManager.beginTransaction().add(R.id.main_framelayout, fragment, i)
            .hide(fragment).commit()
    }

    fun setTitle(title: String) {
        toolbar.title = title
    }

    private fun animateNavigation(fragment: Fragment) {
        val transition = AutoTransition()
        transition.duration = 200
        TransitionManager.beginDelayedTransition(bottomNav, transition)
        val cs = ConstraintSet()
        cs.clone(bottomNav)

        if (fragment is DayFragment) {
            cs.connect(
                R.id.nav_selector,
                ConstraintSet.START,
                R.id.today_action,
                ConstraintSet.START,
                0
            )
            cs.connect(
                R.id.nav_selector,
                ConstraintSet.END,
                R.id.today_action,
                ConstraintSet.END,
                0
            )
            cs.connect(
                R.id.nav_selector,
                ConstraintSet.TOP,
                R.id.today_action,
                ConstraintSet.BOTTOM,
                2
            )
            DrawableCompat.setTint(
                select_today.drawable,
                ContextCompat.getColor(this, R.color.colorPrimary)
            )
            today_text.setTextColor(getColor(R.color.colorPrimary))
        } else {
            DrawableCompat.setTint(
                select_today.drawable,
                ContextCompat.getColor(this, R.color.colorAccent)
            )
            today_text.setTextColor(getColor(R.color.colorAccent))
        }

        if (fragment is WeekFragment) {
            cs.connect(
                R.id.nav_selector,
                ConstraintSet.START,
                R.id.week_action,
                ConstraintSet.START,
                0
            )
            cs.connect(R.id.nav_selector, ConstraintSet.END, R.id.week_action, ConstraintSet.END, 0)
            cs.connect(
                R.id.nav_selector,
                ConstraintSet.TOP,
                R.id.week_action,
                ConstraintSet.BOTTOM,
                2
            )
            DrawableCompat.setTint(
                week_icon.drawable,
                ContextCompat.getColor(this, R.color.colorPrimary)
            )
            week_text.setTextColor(getColor(R.color.colorPrimary))
        } else {
            DrawableCompat.setTint(
                week_icon.drawable,
                ContextCompat.getColor(this, R.color.colorAccent)
            )
            week_text.setTextColor(getColor(R.color.colorAccent))
        }

        if (fragment is ReminderFragment) {
            cs.connect(
                R.id.nav_selector,
                ConstraintSet.START,
                R.id.reminder_action,
                ConstraintSet.START,
                0
            )
            cs.connect(
                R.id.nav_selector,
                ConstraintSet.END,
                R.id.reminder_action,
                ConstraintSet.END,
                0
            )
            cs.connect(
                R.id.nav_selector,
                ConstraintSet.TOP,
                R.id.reminder_action,
                ConstraintSet.BOTTOM,
                2
            )
            DrawableCompat.setTint(
                reminder_icon.drawable,
                ContextCompat.getColor(this, R.color.colorPrimary)
            )
            reminder_text.setTextColor(getColor(R.color.colorPrimary))
        } else {
            reminder_text.setTextColor(getColor(R.color.colorAccent))
            DrawableCompat.setTint(
                reminder_icon.drawable,
                ContextCompat.getColor(this, R.color.colorAccent)
            )
        }

        cs.applyTo(bottomNav)
    }
}
