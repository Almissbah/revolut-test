package com.almissbah.revoluttest.data

import com.almissbah.revoluttest.data.remote.APIService
import com.almissbah.revoluttest.data.remote.model.ApiResponse
import io.reactivex.Observable
import javax.inject.Singleton

@Singleton
class Repository(private val apiService: APIService) {

    fun refreshCurrencies(): Observable<ApiResponse> {
        return apiService.getCurrencies()
    }
}