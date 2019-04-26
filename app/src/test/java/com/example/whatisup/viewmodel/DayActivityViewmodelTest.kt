package com.example.whatisup.viewmodel

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.util.Log
import android.util.TimeUtils
import com.example.whatisup.BuildConfig
import com.example.whatisup.src.data.model.DayActivity
import com.example.whatisup.src.data.repository.MockDayActivityRepository
import com.example.whatisup.src.ui.viewmodel.DayActivityViewModel
import com.example.whatisup.testutils.RxSchedulerRule
import org.junit.*

class DayActivityViewmodelTest {

    @Rule
    @JvmField
    var instantTaskExecutorRule = InstantTaskExecutorRule() // avoids isMainThread() calls in ViewModel init

    @get:Rule
    val rxSchedulerRule = RxSchedulerRule()

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
    fun testSaveActivity() {
        Assert.assertTrue(BuildConfig.IS_TESTING.get())
        val vm = DayActivityViewModel(MockDayActivityRepository())

        vm.saveActivity(DayActivity(date = 9999L))

        Assert.assertTrue(vm.getState().currentDay.date == 9999L)
    }
}