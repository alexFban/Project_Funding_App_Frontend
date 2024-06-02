package com.example.ps2024.model

import com.google.gson.annotations.SerializedName

@Suppress("unused")
class Coord {
    @SerializedName("lat")
    var lat: Double? = null

    @SerializedName("lon")
    var lon: Double? = null
}
