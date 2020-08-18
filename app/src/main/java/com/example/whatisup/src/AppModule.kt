package com.example.whatisup.src

import android.content.Context
import androidx.room.Room
import com.example.whatisup.src.data.AppDatabase
import com.example.whatisup.src.data.DB_NAME
import com.example.whatisup.src.data.dao.DayActivityDao
import com.example.whatisup.src.data.dao.ReminderDao
import com.example.whatisup.src.data.repository.DayActivityRepository
import com.example.whatisup.src.data.repository.DayActivityRepositoryImpl
import com.example.whatisup.src.data.repository.ReminderRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideDb(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java, DB_NAME
        ).build()

    @Provides
    fun provideDayActivityDao(db: AppDatabase): DayActivityDao = db.getDayActivityDao()

    @Provides
    fun provideReminderDao(db: AppDatabase): ReminderDao = db.getReminderDao()

    @Provides
    fun provideDayActivityRepository(dao: DayActivityDao): DayActivityRepository =
        DayActivityRepositoryImpl(dao)

    @Provides
    fun provideReminderRepo(dao: ReminderDao): ReminderRepository =
        ReminderRepository(dao)
}