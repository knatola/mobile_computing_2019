package com.example.whatisup.src.utils

import android.content.Context
import com.example.whatisup.src.data.AppDatabase
import com.example.whatisup.src.data.DayActivityRepository
import com.example.whatisup.src.ui.viewmodel.DayActivityViewModelFactory

object Injection {

    fun provideDayActivityRepo(context: Context): DayActivityRepository {
        return DayActivityRepository(AppDatabase.getAppDataBase(context)!!)
    }

    fun provideDayActivityVmFactory(context: Context) : DayActivityViewModelFactory {
        return DayActivityViewModelFactory(provideDayActivityRepo(context))
    }
}