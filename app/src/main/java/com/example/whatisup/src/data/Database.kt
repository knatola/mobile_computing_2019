package com.example.whatisup.src.data

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import com.example.whatisup.BuildConfig
import com.example.whatisup.src.data.dao.DayActivityDao
import com.example.whatisup.src.data.dao.ReminderDao
import com.example.whatisup.src.data.model.DayActivity
import com.example.whatisup.src.data.model.Reminder
import com.example.whatisup.src.utils.createMockReminders
import com.example.whatisup.src.utils.createMocks
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

@Database(entities = [DayActivity::class, Reminder::class], version = 1)
@TypeConverters(ListConverter::class)
abstract class AppDatabase: RoomDatabase() {
    
    abstract fun getDayActivityDao(): DayActivityDao

    abstract fun getReminderDao(): ReminderDao

    companion object {
        var INSTANCE: AppDatabase? = null

        fun getAppDataBase(context: Context): AppDatabase? {
            if (INSTANCE == null){
                synchronized(AppDatabase::class){
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        AppDatabase::class.java, "testdb").build()
                }
            }

            if (BuildConfig.DEBUG) {
                val mockList = createMocks()
                mockList.forEach {
                    Completable.fromAction { INSTANCE!!.getDayActivityDao().insert(it) }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe {  }
                }

                val mockReminders = createMockReminders()
                mockReminders.forEach {
                    Completable.fromAction { INSTANCE!!.getReminderDao().insert(it) }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe {  }
                }
            }

            return INSTANCE
        }
    }
}