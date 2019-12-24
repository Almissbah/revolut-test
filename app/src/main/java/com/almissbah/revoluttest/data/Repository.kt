package com.almissbah.revoluttest.data

import com.almissbah.revoluttest.data.remote.APIService
import com.almissbah.revoluttest.data.remote.model.CurrencyRatesResponse
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Repository(private val apiService: APIService) {

    fun refreshCurrencies(): Observable<CurrencyRatesResponse> {
        return apiService.getCurrencies()
    }
}