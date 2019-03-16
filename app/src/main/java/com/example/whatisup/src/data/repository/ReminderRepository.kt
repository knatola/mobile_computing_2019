package com.example.whatisup.src.data.repository

import com.example.whatisup.src.data.AppDatabase
import com.example.whatisup.src.data.model.Reminder
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ReminderRepository(private val db: AppDatabase) {

    fun getReminders(): Single<List<Reminder>> {
        return db.getReminderDao().getAll()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
    }

    fun createReminder(reminder: Reminder): Completable {
        return Completable.fromAction { db.getReminderDao().insert(reminder)}
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
    }
}