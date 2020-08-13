package com.example.whatisup.src.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.whatisup.src.data.repository.DayActivityRepository

class DayActivityViewModelFactory(private val dayActivityRepository: DayActivityRepository)
    : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DayActivityViewModel::class.java)) {
            return DayActivityViewModel(dayActivityRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}