package com.example.whatisup.src.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.widget.TextView
import com.example.whatisup.R
import com.example.whatisup.src.data.ActivityProvider
import com.example.whatisup.src.utils.TimeUtils
import com.example.whatisup.src.utils.stringDate
import kotlinx.android.synthetic.main.activity_main.*

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

        toolbar = findViewById(R.id.toolbar)
        toolbar.title = "Today"
        toolbar.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        toolbar.setTitleTextColor(this.getColor(R.color.black))

        ActivityProvider.requestUpdates(this)

        bottom_navigation.setOnNavigationItemSelectedListener {

            when (it.itemId) {
                R.id.action_today -> {
                    loadFragment(dayFragment, stringDate(TimeUtils.getTodayLong()))
                }
                R.id.action_week -> {
                    loadFragment(weekFragment, "Week") //todo hardcoded
                }
                R.id.action_all -> {
                    loadFragment(reminderFragment, "Reminders") //todo hardcoded
                }
            }
            return@setOnNavigationItemSelectedListener true
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
}
