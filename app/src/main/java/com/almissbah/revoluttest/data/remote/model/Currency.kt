package com.almissbah.revoluttest.data.remote.model


data class Currency(var name: String, var rate: Double) {
    var value: Double = 0.0

    var valueToString = "0.0"
        get() {
            return value.toString()
        }

    fun calculateValue(baseValue: Double) {
        value = baseValue * rate
    }

    override fun equals(other: Any?): Boolean {
        if (other is Currency)
            return other.name == this.name
        return false
    }


    fun contentEquals(other: Any?): Boolean {
        if (other is Currency)
            if (other.value == this.value)
                return true
        return false
    }
}