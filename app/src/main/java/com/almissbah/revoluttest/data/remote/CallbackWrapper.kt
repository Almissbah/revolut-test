package com.almissbah.revoluttest.data.remote

import android.util.Log
import io.reactivex.observers.DisposableObserver

abstract class CallbackWrapper<T> :
    DisposableObserver<T>() {
    protected abstract fun onSuccess(t: T)
    override fun onNext(t: T) {
        onSuccess(t)
    }

    override fun onError(e: Throwable) {
        Log.d(CallbackWrapper::class.java.simpleName, e.toString())
    }

    override fun onComplete() {}
}