package com.almissbah.revoluttest.ui.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.almissbah.revoluttest.data.Repository
import com.almissbah.revoluttest.data.remote.CallbackWrapper
import com.almissbah.revoluttest.data.remote.model.ApiResponse
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import com.almissbah.revoluttest.data.remote.model.Currency
import com.almissbah.revoluttest.utils.BASE_RATE
import com.almissbah.revoluttest.utils.REFRESH_PERIOD
import java.util.concurrent.TimeUnit

class CurrenciesViewModel : ViewModel() {
    val currenciesLiveData: MutableLiveData<List<Currency>> = MutableLiveData()
    lateinit var repository: Repository
    lateinit var subscription: Disposable
    var baseValue: Double = 1.0
    fun subscribe(): MutableLiveData<List<Currency>> {
        subscription = buildSubscription()
        return currenciesLiveData
    }

    private fun buildSubscription(): Disposable {

        return Observable.interval(REFRESH_PERIOD, TimeUnit.SECONDS, Schedulers.io())
            .observeOn(Schedulers.io())
            .map { t -> repository.refreshCurrencies() }.subscribe { t ->
                t.subscribe(object : CallbackWrapper<ApiResponse>() {
                    override fun onSuccess(t: ApiResponse) {
                        buildData(t)
                    }
                })
            }
    }

    private fun buildData(data: ApiResponse) {
        val currencyList = mutableListOf<Currency>()
        val dataSet = data.rates.entries
        currencyList.add(Currency(data.base, BASE_RATE))
        dataSet.iterator().forEach { currency ->
            currencyList.add(Currency(currency.key, currency.value))
            //    Log.i("data currencies 1"+currency.key," value="+currency.value)
        }
        // currenciesLiveData.value=currencyList
        currenciesLiveData.postValue(currencyList)
    }

    fun unSubscribe() {
        if (!subscription.isDisposed) subscription.dispose()
    }
}
