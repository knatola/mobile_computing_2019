package com.example.whatisup.src.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import com.example.whatisup.src.data.model.Reminder
import com.example.whatisup.src.data.repository.ReminderRepository

private const val TAG = "ReminderViewModel"

data class ReminderViewState(val reminders: List<Reminder> = listOf())

class ReminderViewModel @ViewModelInject constructor(private val reminderRepository: ReminderRepository) :
    BaseViewModel<ReminderViewState>() {

    init {
        setState(ReminderViewState())
    }

    fun getAllReminders() {
        addDisposable(
            reminderRepository.getReminders()
                .subscribe({ data ->
                    setState(ReminderViewState(data))
                }, { e ->
                    Log.w(TAG, "Error getting reminders", e)
                })
        )
    }

    fun deleteReminder(reminder: Reminder) {
        reminderRepository.deleteReminder(reminder)
            .doOnComplete { getAllReminders() }
            .subscribe()
    }

    class Factory(private val reminderRepository: ReminderRepository) : ViewModelProvider.Factory {

        @Suppress("unchecked_cast")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ReminderViewModel::class.java)) {
                return ReminderViewModel(reminderRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

