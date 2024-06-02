package com.example.ps2024.model

import com.google.gson.annotations.SerializedName

@Suppress("unused")
class Main {
    @SerializedName("humidity")
    var humidity: Long? = null

    @SerializedName("pressure")
    var pressure: Long? = null

    @SerializedName("temp")
    var temp: Double? = null

    @SerializedName("temp_max")
    var tempMax: Double? = null

    @SerializedName("temp_min")
    var tempMin: Double? = null
}
