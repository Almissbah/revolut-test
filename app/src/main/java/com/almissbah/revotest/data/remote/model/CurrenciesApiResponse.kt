package com.almissbah.revotest.data.remote.model

data class CurrenciesApiResponse(var base: String, var date: String, var rates: Map<String, Double>)