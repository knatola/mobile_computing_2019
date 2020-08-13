package com.example.whatisup.src.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseViewModel<T> : ViewModel() {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    open val liveState = MutableLiveData<T>()

    fun setState(state: T) {
        this.liveState.value = state
    }

    fun getState() = this.liveState.value!! // !! here is problematic

    fun state() = this.liveState

    protected fun addDisposable(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    private fun clearDisposables() {
        compositeDisposable.clear()
    }

    override fun onCleared() {
        clearDisposables()
    }
}