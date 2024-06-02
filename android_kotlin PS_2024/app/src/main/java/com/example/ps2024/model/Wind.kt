package com.example.ps2024.model

import com.google.gson.annotations.SerializedName

@Suppress("unused")
class Wind {
    @SerializedName("deg")
    var deg: Long? = null

    @SerializedName("speed")
    var speed: Double? = null
}
