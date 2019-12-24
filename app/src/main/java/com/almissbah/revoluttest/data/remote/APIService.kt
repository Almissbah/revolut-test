package com.almissbah.revoluttest.data.remote

import com.almissbah.revoluttest.data.remote.model.CurrencyRatesResponse
import io.reactivex.Observable
import retrofit2.http.GET


interface APIService {
    @GET(value = "/latest?base=EUR")
    fun getCurrencies(): Observable<CurrencyRatesResponse>
}