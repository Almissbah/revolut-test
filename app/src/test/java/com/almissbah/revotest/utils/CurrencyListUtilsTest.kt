package com.almissbah.revotest.utils

import com.almissbah.revotest.data.remote.model.Currency
import org.junit.Assert.*
import org.junit.Test

class CurrencyListUtilsTest {
    val sampleResponce1 =
        "{\"base\":\"EUR\",\"date\":\"2018-09-06\",\"rates\":{\"AUD\":1.6164,\"BGN\":1.9558,\"BRL\":4.7919,\"CAD\":1.5338,\"CHF\":1.1275,\"CNY\":7.9452,\"CZK\":25.715,\"DKK\":7.4568,\"GBP\":0.89825,\"HKD\":9.1325,\"HRK\":7.4342,\"HUF\":326.49,\"IDR\":17324.0,\"ILS\":4.1707,\"INR\":83.719,\"ISK\":127.8,\"JPY\":129.55,\"KRW\":1304.8,\"MXN\":22.366,\"MYR\":4.8121,\"NOK\":9.7761,\"NZD\":1.7633,\"PHP\":62.593,\"PLN\":4.3184,\"RON\":4.6386,\"RUB\":79.576,\"SEK\":10.591,\"SGD\":1.6,\"THB\":38.131,\"TRY\":7.6283,\"USD\":1.1634,\"ZAR\":17.824}}"
    val sampleResponce2 =
        "{\"base\":\"EUR\",\"date\":\"2018-09-06\",\"rates\":{\"HKD\":9.1329,\"HRK\":7.4345,\"AUD\":1.6165,\"BGN\":1.9559,\"BRL\":4.7921,\"CAD\":1.5339,\"CHF\":1.1276,\"CNY\":7.9456,\"CZK\":25.716,\"DKK\":7.4571,\"GBP\":0.89829,\"HUF\":326.51,\"IDR\":17325.0,\"ILS\":4.1708,\"INR\":83.722,\"ISK\":127.81,\"JPY\":129.56,\"KRW\":1304.8,\"MXN\":22.367,\"MYR\":4.8123,\"NOK\":9.7766,\"NZD\":1.7634,\"PHP\":62.596,\"PLN\":4.3185,\"RON\":4.6388,\"RUB\":79.579,\"SEK\":10.591,\"SGD\":1.6001,\"THB\":38.132,\"TRY\":7.6286,\"USD\":1.1635,\"ZAR\":17.824}}"
    val delta = 0.0

    @Test
    fun testCovertStringToDouble() {
        assertEquals("Should equal 0.0", CurrencyListUtils.toValidDouble(""), 0.0, delta)
        assertEquals("Should equal 0.0", CurrencyListUtils.toValidDouble("."), 0.0, delta)
        assertEquals("Should equal 55.5", CurrencyListUtils.toValidDouble("55"), 55.0, delta)
    }

    @Test
    fun testCalculateBaseValue() {
        assertEquals(CurrencyListUtils.calculateBaseValue(1.1, Currency("USD", 1.0)), 1.1, delta)
        assertEquals(CurrencyListUtils.calculateBaseValue(15.0, Currency("USD", 5.0)), 3.0, delta)
        assertEquals(CurrencyListUtils.calculateBaseValue(1.1, null), 1.1, delta)
    }

    @Test
    fun testParseResponseFromJson() {
        val response = CurrencyListUtils.parseResponseFromJson(sampleResponce1)
        assertEquals(response!!.base, "EUR")
        assertEquals(response.rates.size, 32)
    }


    @Test
    fun testGenerateListFromResponse() {
        val response = CurrencyListUtils.parseResponseFromJson(sampleResponce1)
        val list = CurrencyListUtils.generateListFromResponse(response!!)
        assertEquals(list.first().name, "EUR")
        assertEquals(list.size, 33)
    }

    @Test
    fun testGetValidBaseCurrency() {
        val response = CurrencyListUtils.parseResponseFromJson(sampleResponce1)
        val list = CurrencyListUtils.generateListFromResponse(response!!)
        assertEquals(CurrencyListUtils.getValidBaseCurrency(1.0, null, list).name, "EUR")
        assertEquals(
            CurrencyListUtils.getValidBaseCurrency(1.0, Currency("USD", 1.1), list).name,
            "USD"
        )
    }

    @Test
    fun testCalculateCorrespondingValues() {
        val response = CurrencyListUtils.parseResponseFromJson(sampleResponce1)
        val list = CurrencyListUtils.generateListFromResponse(response!!)
        val newList = CurrencyListUtils.calculateCorrespondingValues(1.0, list)

        list.first().calculateValue(2.0)
        assertEquals(list.first().value, newList.first().value * 2, delta)

        list[5].calculateValue(2.0)
        assertEquals(list[5].value, newList[5].value * 2, delta)
    }


    @Test
    fun testCopyListItemsOrder() {
        val response1 = CurrencyListUtils.parseResponseFromJson(sampleResponce1)
        val response2 = CurrencyListUtils.parseResponseFromJson(sampleResponce2)
        val list1 = CurrencyListUtils.generateListFromResponse(response1!!)
        val list2 = CurrencyListUtils.generateListFromResponse(response2!!)

        val orderedList = CurrencyListUtils.copyListItemsOrder(fromList = list2, toList = list1)

        assertEquals(orderedList[1].name, list2[1].name)
        assertEquals(orderedList[1].value, list2[1].value, delta)
    }
}