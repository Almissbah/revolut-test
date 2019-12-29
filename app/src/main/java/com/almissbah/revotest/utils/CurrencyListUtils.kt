package com.almissbah.revotest.utils

import com.almissbah.revotest.data.remote.model.CurrenciesApiResponse
import com.almissbah.revotest.data.remote.model.Currency
import com.google.gson.Gson

class CurrencyListUtils {

    companion object {

        /**
         * Converts user input value to a valid double
         *
         * @param text user input as string.
         * @return 0.0 if string is invalid double or converted double otherwise.
         */
        fun toValidDouble(text: String): Double {
            var value = 0.0
            if (text != "" && text != ".") value = java.lang.Double.parseDouble(text)
            return value
        }

        /**
         * Calculates EUR value from current base currency
         *
         * @param userInput
         * @param currency current base currency
         * @return value in EUR
         */
        fun calculateBaseValue(userInput: Double, currency: Currency?): Double {
            return userInput / (currency?.rate ?: 1.0)
        }


        /**
         * Build response object from json string
         *
         * @param jsonString
         * @return CurrenciesApiResponse instance
         */
        fun parseResponseFromJson(jsonString: String): CurrenciesApiResponse? {
            return Gson().fromJson<CurrenciesApiResponse>(
                jsonString,
                CurrenciesApiResponse::class.java
            )
        }


        /**
         * Build currencies list from response object
         *
         * @param response CurrenciesApiResponse instance
         * @return MutableList<Currency> instance
         */
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


        /**
         * Search the list for baseCurrency name to update currency the value and returns the first
         * element from the list if baseCurrency name not found.
         *
         * @param baseValue current base value in EUR
         * @param baseCurrency current base currency
         * @param currencies updated currency list
         * @return
         */
        fun getValidBaseCurrency(
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
            return currency
        }


        /**
         * Updates list elements with a new base value in EUR and create a new instances for the same values.
         *
         * @param baseValue base value in EUR
         * @param forList
         * @return updated list
         */
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

        /**
         * Generates a new list by ordering its elements base on the old list order.
         *
         * @param fromList list to copy order from
         * @param toList list to order by fromlist
         * @return
         */
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