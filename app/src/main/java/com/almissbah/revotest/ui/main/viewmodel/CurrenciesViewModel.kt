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
import com.almissbah.revotest.utils.CurrencyListUtils
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
            .map { mRevoRepository.getCurrencies() }.subscribe {
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
                        postResponse(t)
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

    private fun postResponse(response: CurrenciesApiResponse) {
        mCurrenciesList = buildOrderedCurrenciesList(response)
        if (mCurrenciesList.isNotEmpty()) {
            mBaseCurrency = CurrencyListUtils.getValidateBaseCurrency(
                mBaseValue,
                mBaseCurrency,
                mCurrenciesList
            )
            calculateValues(mUserInput)
        } else {
            mCurrenciesLiveData.postValue(Resource(Resource.Status.SUCCESS, mCurrenciesList))
        }
    }

    private fun buildOrderedCurrenciesList(response: CurrenciesApiResponse): MutableList<Currency> {
        val tempList = CurrencyListUtils.generateListFromResponse(response)
        return CurrencyListUtils.copyListItemsOrder(fromList = mCurrenciesList, toList = tempList)
    }


    fun setBaseCurrency(currency: Currency) {
        mBaseCurrency = currency
        mUserInput = currency.value
        calculateValues(mUserInput)
    }

    private fun calculateValues(value: Double) {
        mUserInput = value
        mBaseValue = CurrencyListUtils.calculateBaseValue(mUserInput, mBaseCurrency)
        val list = CurrencyListUtils.calculateCorrespondingValues(mBaseValue, mCurrenciesList)
        mCurrenciesLiveData.postValue(Resource(mStatus, list))
    }

    fun calculateValues(text: String) {
        calculateValues(CurrencyListUtils.toValidDouble(text))
    }


    fun moveItemToTop(
        from: Int,
        currency: Currency
    ) {
        mCurrenciesList.removeAt(from)
        mCurrenciesList.add(0, currency)
        mCurrenciesLiveData.postValue(Resource(Resource.Status.SUCCESS, mCurrenciesList))
    }


    fun unSubscribe() {
        mSubscription?.dispose()
    }
}
