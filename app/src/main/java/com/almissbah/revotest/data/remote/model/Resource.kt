package com.almissbah.revotest.data.remote.model

class Resource(val stats: Status, val currencies: MutableList<Currency>?) {


    enum class Status {
        INIT, LOADING, SUCCESS, FAIL,
    }
}