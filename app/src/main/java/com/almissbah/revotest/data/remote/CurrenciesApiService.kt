package com.almissbah.revotest.data.remote

import com.almissbah.revotest.data.remote.model.CurrenciesApiResponse
import io.reactivex.Observable
import retrofit2.http.GET


interface CurrenciesApiService {
    @GET(value = "/latest?base=EUR")
    fun getCurrencies(): Observable<CurrenciesApiResponse>
}