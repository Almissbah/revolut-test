package com.almissbah.revotest.data

import com.almissbah.revotest.data.remote.CurrenciesApiService
import com.almissbah.revotest.data.remote.model.CurrenciesApiResponse
import io.reactivex.Observable
import javax.inject.Singleton

@Singleton
class RevoRepository(private val currenciesApiService: CurrenciesApiService) {

    fun getCurrencies(): Observable<CurrenciesApiResponse> {
        return currenciesApiService.getCurrencies()
    }
}