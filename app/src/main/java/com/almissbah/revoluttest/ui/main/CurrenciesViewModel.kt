package com.almissbah.revoluttest.ui.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.almissbah.revoluttest.data.Repository
import com.almissbah.revoluttest.data.remote.CallbackWrapper
import com.almissbah.revoluttest.data.remote.model.CurrencyRatesResponse
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit

class CurrenciesViewModel : ViewModel() {
    val currenciesLiveData: MutableLiveData<List<Currency>> = MutableLiveData()
    lateinit var repository: Repository
    lateinit var subscription: Disposable

    fun subscribe(): MutableLiveData<List<Currency>> {
        subscription =
            buildSubscription()

        return currenciesLiveData
    }

    private fun buildSubscription(): Disposable {

        return Observable.interval(2, TimeUnit.SECONDS, Schedulers.io()).observeOn(Schedulers.io())
            .map { t -> repository.refreshCurrencies() }.subscribe { t ->
                t.subscribe(object : CallbackWrapper<CurrencyRatesResponse>() {
                    override fun onSuccess(t: CurrencyRatesResponse) {
                        Log.i("data currencies", t.toString());
                    }
                })
            }
    }

    private fun buildData(data: CurrencyRatesResponse) {
        var currencyList = mutableListOf<Currency>()

        // currencyList.add()
    }

    fun unSubscribe() {
        if (!subscription.isDisposed) subscription.dispose()
    }
}
