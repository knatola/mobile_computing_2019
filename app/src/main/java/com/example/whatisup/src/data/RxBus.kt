package com.example.whatisup.src.data

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

object RxBus {

    private val subject: PublishSubject<Any> = PublishSubject.create()

    fun <T> listen(action: Class<T>): Observable<T> = subject.ofType(action)

    fun publish(message: Any) {
        subject.onNext(message)
    }
}