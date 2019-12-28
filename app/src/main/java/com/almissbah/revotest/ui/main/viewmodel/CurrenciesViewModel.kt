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
import com.almissbah.revotest.data.remote.model.Resource
import com.almissbah.revotest.utils.BASE_RATE
import com.almissbah.revotest.utils.INITIAL_DELAY
import com.almissbah.revotest.utils.REFRESH_PERIOD
import java.util.concurrent.TimeUnit

class CurrenciesViewModel : ViewModel() {

    private val mCurrenciesLiveData: MutableLiveData<Resource> = MutableLiveData()
    lateinit var mRevoRepository: RevoRepository
    private var mSubscription: Disposable? = null
    private var mCurrenciesList: MutableList<Currency> = mutableListOf()
    private var mBaseValue: Double = 1.0
    private var mUserInput: Double = 1.0
    private var mBaseCurrency: Currency? = null
    private var mStatus = Resource.Status.INIT

    fun subscribe() {
        if (mSubscription == null || mSubscription!!.isDisposed) mSubscription = buildSubscription()
    }

    fun getCurrencies(): MutableLiveData<Resource> {
        return mCurrenciesLiveData
    }
    private fun buildSubscription(): Disposable {
        return Observable.interval(INITIAL_DELAY, REFRESH_PERIOD, TimeUnit.SECONDS, Schedulers.io())
            .observeOn(Schedulers.io())
            .map { mRevoRepository.refreshCurrencies() }.subscribe {
                it.subscribe(object : CallbackWrapper<CurrenciesApiResponse>() {
                    override fun onStart() {
                        postLoading()
                    }

                    override fun onNext(t: CurrenciesApiResponse) {
                        postLoading()
                        onSuccess(t)
                    }

                    override fun onError(e: Throwable) {
                        mStatus = Resource.Status.FAIL
                        postError()
                    }
                    override fun onSuccess(t: CurrenciesApiResponse) {
                        mStatus = Resource.Status.SUCCESS
                        parseResponse(t)
                    }
                })
            }
    }

    private fun postError() {
        mCurrenciesLiveData.postValue(Resource(Resource.Status.FAIL, null))
    }

    private fun postLoading() {
        mCurrenciesLiveData.postValue(Resource(Resource.Status.LOADING, null))
    }

    private fun parseResponse(responseCurrencies: CurrenciesApiResponse) {
        mCurrenciesList = buildCurrenciesList(responseCurrencies)
        validateBaseCurrency()
        calculateValues(mUserInput)
    }

    private fun buildCurrenciesList(responseCurrencies: CurrenciesApiResponse): MutableList<Currency> {
        val tempList = mutableListOf<Currency>()
        val dataSet = responseCurrencies.rates.entries
        val currency = Currency(responseCurrencies.base, BASE_RATE) // Base currency
        tempList.add(currency)
        dataSet.iterator().forEach { jsonEntry ->
            val newCurrency = Currency(jsonEntry.key, jsonEntry.value)
            tempList.add(newCurrency)
        }
        return arrangeCurrencies(tempList)
    }

    private fun validateBaseCurrency() {
        if (mBaseCurrency == null) {
            mCurrenciesList[0].calculateValue(mBaseValue)
            mBaseCurrency = mCurrenciesList[0]
        } else {
            mBaseCurrency = mCurrenciesList.find { it.name == mBaseCurrency!!.name }
            if (mBaseCurrency == null) {
                mCurrenciesList[0].calculateValue(mBaseValue)
                mBaseCurrency = mCurrenciesList[0]
            }
        }
    }


    fun updateBaseCurrency(currency: Currency) {
        mBaseCurrency = currency
        mUserInput = currency.value
        calculateValues(mUserInput)
    }

    private fun calculateValues(value: Double) {
        mUserInput = value
        mBaseValue = mUserInput / if (mBaseCurrency != null) mBaseCurrency!!.rate else 1.0
        val list = mutableListOf<Currency>()
        mCurrenciesList.forEach {
            val currency = Currency(it.name, it.rate)
            currency.calculateValue(mBaseValue)
            list.add(currency)
        }
        mCurrenciesLiveData.postValue(Resource(mStatus, list))
    }

    fun calculateValues(text: String) {
        calculateValues(toValidDouble(text))
    }

    private fun toValidDouble(text: String): Double {
        var value = 0.0
        if (text != "" && text != ".") value = java.lang.Double.parseDouble(text)
        return value
    }

    fun moveItemToTop(
        from: Int,
        currency: Currency
    ) {
        mCurrenciesList.removeAt(from)
        mCurrenciesList.add(0, currency)
        mCurrenciesLiveData.postValue(Resource(Resource.Status.SUCCESS, mCurrenciesList))
    }

    private fun arrangeCurrencies(currencies: MutableList<Currency>): MutableList<Currency> {
        val tempList = mutableListOf<Currency>()
        if (mCurrenciesList.size > 0) mCurrenciesList.forEach { currency ->
            val item: Currency? =
                currencies.find { t -> t.name == currency.name }
            if (item != null) {
                tempList.add(item)
                currencies.remove(item)
            }
        }
        tempList.addAll(currencies)
        return tempList
    }

    fun unSubscribe() {
        mSubscription?.dispose()
    }
}
