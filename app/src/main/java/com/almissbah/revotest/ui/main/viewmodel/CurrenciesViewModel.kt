package com.almissbah.revotest.ui.main.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.almissbah.revotest.data.RevoRepository
import com.almissbah.revotest.data.remote.CallbackWrapper
import com.almissbah.revotest.data.remote.model.CurrenciesApiResponse
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import com.almissbah.revotest.data.remote.model.Currency
import com.almissbah.revotest.utils.BASE_RATE
import com.almissbah.revotest.utils.INITIAL_DELAY
import com.almissbah.revotest.utils.REFRESH_PERIOD
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

class CurrenciesViewModel : ViewModel() {
    private val currenciesLiveData: MutableLiveData<MutableList<Currency>> = MutableLiveData()
    lateinit var revoRepository: RevoRepository
    private var subscription: Disposable? = null
    private var mCurrenciesList: MutableList<Currency> = mutableListOf()
    private var baseValue: Double = 1.0
    private var userInput: Double = 1.0
    private var baseCurrency: Currency? = null

    fun subscribe(): MutableLiveData<MutableList<Currency>> {
        if (subscription == null || subscription!!.isDisposed) subscription = buildSubscription()
        return currenciesLiveData
    }

    private fun buildSubscription(): Disposable {
        return Observable.interval(INITIAL_DELAY, REFRESH_PERIOD, TimeUnit.SECONDS, Schedulers.io())
            .observeOn(Schedulers.io())
            .map { revoRepository.refreshCurrencies() }.subscribe {
                it.subscribe(object : CallbackWrapper<CurrenciesApiResponse>() {
                    override fun onSuccess(t: CurrenciesApiResponse) {
                        parseResponse(t)
                    }
                })
            }
    }

    private fun parseResponse(responseCurrencies: CurrenciesApiResponse) {
        mCurrenciesList = buildList(responseCurrencies)
        validateBaseCurrency()
        calculateValues(userInput)
    }

    private fun buildList(responseCurrencies: CurrenciesApiResponse): MutableList<Currency> {
        val tempList = mutableListOf<Currency>()
        val dataSet = responseCurrencies.rates.entries
        val currency = Currency(responseCurrencies.base, BASE_RATE) // Base currency
        tempList.add(currency)
        dataSet.iterator().forEach { jsonEntry ->
            val newCurrency = Currency(jsonEntry.key, jsonEntry.value)
            tempList.add(newCurrency)
        }
        return tempList
    }

    private fun validateBaseCurrency() {
        if (baseCurrency == null) {
            mCurrenciesList[0].calculateValue(baseValue)
            baseCurrency = mCurrenciesList[0]
        } else {
            baseCurrency = mCurrenciesList.find { it.name == baseCurrency!!.name }
            if (baseCurrency == null) {
                mCurrenciesList[0].calculateValue(baseValue)
                baseCurrency = mCurrenciesList[0]
            }
        }
    }


    fun updateBaseCurrency(currency: Currency) {
        baseCurrency = currency
        userInput = currency.value
        calculateValues(userInput)
    }

    private fun calculateValues(value: Double) {
        userInput = value
        baseValue = userInput / baseCurrency!!.rate
        val list = mutableListOf<Currency>()
        mCurrenciesList.forEach {
            val currency = Currency(it.name, it.rate)
            currency.value = ((baseValue * currency.rate) * 100).roundToInt() / 100.0
            list.add(currency)
        }
        currenciesLiveData.postValue(list)
    }

    fun calculateValues(text: String) {
        var value = 0.0
        if (text != "" && text != ".") value = java.lang.Double.parseDouble(text)
        calculateValues(value)
    }

    fun unSubscribe() {
        subscription!!.dispose()
    }
}