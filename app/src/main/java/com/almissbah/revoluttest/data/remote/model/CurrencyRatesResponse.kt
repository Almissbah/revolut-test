package com.almissbah.revoluttest.data.remote.model

import org.json.JSONObject

data class CurrencyRatesResponse(var base: String, var date: String, var rates: JSONObject)