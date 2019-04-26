package com.example.whatisup.src.utils

import android.content.Context
import com.example.whatisup.BuildConfig
import com.example.whatisup.src.BaseApp
import com.example.whatisup.src.data.AppDatabase
import com.example.whatisup.src.data.repository.DayActivityRepository
import com.example.whatisup.src.data.repository.MockDayActivityRepository
import com.example.whatisup.src.data.repository.ReminderRepository
import com.example.whatisup.src.ui.viewmodel.DayActivityViewModelFactory
import com.example.whatisup.src.ui.viewmodel.ReminderViewModel

/**
 * DI helper object, here we can choose what implementations we pass to ViewModels
 *
 */
object Injection {

    fun provideDayActivityRepo(): DayActivityRepository {
        return when (BuildConfig.IS_TESTING.get()) {
            true -> MockDayActivityRepository()
            false -> BaseApp.dayActivityRepository
        }
    }

    fun provideDayActivityVmFactory(): DayActivityViewModelFactory {
        return DayActivityViewModelFactory(provideDayActivityRepo())
    }

    fun provideReminderRepo(context: Context): ReminderRepository {
        return ReminderRepository(AppDatabase.getAppDataBase(context)!!)
    }

    fun provideReminderVmFactory(context: Context): ReminderViewModel.Factory {
        return ReminderViewModel.Factory(provideReminderRepo(context))
    }
}