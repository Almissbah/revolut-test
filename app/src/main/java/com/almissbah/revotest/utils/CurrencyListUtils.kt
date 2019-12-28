package com.almissbah.revotest.utils

import com.almissbah.revotest.data.remote.model.CurrenciesApiResponse
import com.almissbah.revotest.data.remote.model.Currency

class CurrencyListUtils {
    companion object {
        fun toValidDouble(text: String): Double {
            var value = 0.0
            if (text != "" && text != ".") value = java.lang.Double.parseDouble(text)
            return value
        }

        fun calculateBaseValue(userInput: Double, currency: Currency?): Double {
            return userInput / (currency?.rate ?: 1.0)
        }

        fun getValidateBaseCurrency(
            baseValue: Double,
            baseCurrency: Currency?,
            currencies: MutableList<Currency>
        ): Currency {
            var currency = baseCurrency
            if (currency == null) {
                currencies.first().calculateValue(baseValue)
                currency = currencies.first()
            } else {
                currency = currencies.find { it.name == currency!!.name }
                if (currency == null) {
                    currencies.first().calculateValue(baseValue)
                    currency = currencies.first()
                }
            }
            return currency;
        }

        fun calculateCorrespondingValues(
            baseValue: Double,
            forList: MutableList<Currency>
        ): MutableList<Currency> {
            val list = mutableListOf<Currency>()
            forList.forEach {
                val currency = Currency(it.name, it.rate)
                currency.calculateValue(baseValue)
                list.add(currency)
            }
            return list
        }

        fun generateListFromResponse(response: CurrenciesApiResponse): MutableList<Currency> {
            val tempList = mutableListOf<Currency>()
            val dataSet = response.rates.entries
            val currency = Currency(response.base, BASE_RATE) // Base currency
            tempList.add(currency)
            dataSet.iterator().forEach { jsonEntry ->
                val newCurrency = Currency(jsonEntry.key, jsonEntry.value)
                tempList.add(newCurrency)
            }
            return tempList
        }

        fun copyListItemsOrder(
            fromList: MutableList<Currency>,
            toList: MutableList<Currency>
        ): MutableList<Currency> {
            val tempList = mutableListOf<Currency>()
            if (fromList.size > 0) fromList.forEach { currency ->
                val item: Currency? =
                    toList.find { t -> t.name == currency.name }
                if (item != null) {
                    tempList.add(item)
                    toList.remove(item)
                }
            }
            tempList.addAll(toList)
            return tempList
        }
    }
}