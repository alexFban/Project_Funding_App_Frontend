package com.example.ps2024.model

import com.google.gson.annotations.SerializedName

@Suppress("unused")
class Sys {
    @SerializedName("country")
    var country: String? = null

    @SerializedName("id")
    var id: Long? = null

    @SerializedName("message")
    var message: Double? = null

    @SerializedName("sunrise")
    var sunrise: Long? = null

    @SerializedName("sunset")
    var sunset: Long? = null

    @SerializedName("type")
    var type: Long? = null
}
