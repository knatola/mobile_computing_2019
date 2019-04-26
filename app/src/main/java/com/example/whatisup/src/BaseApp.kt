package com.example.whatisup.src

import android.app.Application
import android.content.Context
import com.example.whatisup.src.data.AppDatabase
import com.example.whatisup.src.data.repository.DayActivityRepository
import com.example.whatisup.src.data.repository.DayActivityRepositoryImpl
import com.example.whatisup.src.utils.NotificationProvider

class BaseApp : Application() {

    companion object MODULE {

        lateinit var dayActivityRepository: DayActivityRepository
        lateinit var notificationProvider: NotificationProvider

        fun initRepos(context: Context) {
            dayActivityRepository = DayActivityRepositoryImpl(AppDatabase.getAppDataBase(context)!!)
            notificationProvider = NotificationProvider(context)
        }
    }

    override fun onCreate() {
        super.onCreate()
        initRepos(this)
    }
}