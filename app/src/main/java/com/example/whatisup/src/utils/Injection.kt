package com.example.whatisup.src.utils

import android.content.Context
import com.example.whatisup.src.data.AppDatabase
import com.example.whatisup.src.data.repository.DayActivityRepository
import com.example.whatisup.src.data.repository.ReminderRepository
import com.example.whatisup.src.ui.viewmodel.DayActivityViewModelFactory
import com.example.whatisup.src.ui.viewmodel.ReminderViewModel

object Injection {

    fun provideDayActivityRepo(context: Context): DayActivityRepository {
        return DayActivityRepository(AppDatabase.getAppDataBase(context)!!)
    }

    fun provideDayActivityVmFactory(context: Context): DayActivityViewModelFactory {
        return DayActivityViewModelFactory(provideDayActivityRepo(context))
    }

    fun provideReminderRepo(context: Context): ReminderRepository {
        return ReminderRepository(AppDatabase.getAppDataBase(context)!!)
    }

    fun provideReminderVmFactory(context: Context): ReminderViewModel.Factory {
        return ReminderViewModel.Factory(provideReminderRepo(context))
    }
}