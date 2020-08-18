package com.example.whatisup.src.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
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

const val DB_NAME = "local_db"

@Database(entities = [DayActivity::class, Reminder::class], version = 1)
@TypeConverters(ListConverter::class)
abstract class AppDatabase: RoomDatabase() {
    
    abstract fun getDayActivityDao(): DayActivityDao

    abstract fun getReminderDao(): ReminderDao

}