package com.example.whatisup.src.ui.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.example.whatisup.src.data.DayActivityRepository

class DayActivityViewModelFactory(private val dayActivityRepository: DayActivityRepository)
    : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DayActivityViewModel::class.java)) {
            return DayActivityViewModel(dayActivityRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}