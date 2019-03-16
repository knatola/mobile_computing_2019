package com.example.whatisup.src.ui.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.util.Log
import com.example.whatisup.src.data.model.Reminder
import com.example.whatisup.src.data.repository.ReminderRepository

private const val TAG = "ReminderViewModel"

class ReminderViewModel(private val reminderRepository: ReminderRepository) : BaseViewModel() {

    var reminders = MutableLiveData<List<Reminder>>()

    init {
        reminders.value = listOf()
    }

    fun getAllReminders() {
        addDisposable(reminderRepository.getReminders()
            .subscribe({ data ->
                reminders.value = data
            }, { e ->
                Log.w(TAG, "Error getting reminders", e)
            })
        )
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

