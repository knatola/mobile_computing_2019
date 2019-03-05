package com.example.whatisup.src.ui.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.example.whatisup.src.data.common.State
import com.example.whatisup.src.data.common.Status
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

open class BaseViewModel : ViewModel() {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    val liveState = MutableLiveData<State>()

    init {
        liveState.value = State(Status.SUCCESSFUL) // todo: base state could rather be IDLE or something
    }

    fun setState(state: State) {
        this.liveState.value = state
    }

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