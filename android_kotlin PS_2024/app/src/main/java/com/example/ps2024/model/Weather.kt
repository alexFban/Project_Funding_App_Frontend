package com.example.ps2024.model

import com.google.gson.annotations.SerializedName

@Suppress("unused")
class Weather {
    @SerializedName("base")
    var base: String? = null

    @SerializedName("clouds")
    var clouds: Clouds? = null

    @SerializedName("cod")
    var cod: Long? = null

    @SerializedName("coord")
    var coord: Coord? = null

    @SerializedName("description")
    var description: String? = null

    @SerializedName("dt")
    var dt: Long? = null

    @SerializedName("icon")
    var icon: String? = null

    @SerializedName("id")
    var id: Long? = null

    @SerializedName("main")
    var main: Main? = null

    @SerializedName("name")
    var name: String? = null

    @SerializedName("sys")
    var sys: Sys? = null

    @SerializedName("visibility")
    var visibility: Long? = null

    //    public List<Weather> getWeather() {
    //        return mWeather;
    //    }
    //
    //    public void setWeather(List<Weather> weather) {
    //        mWeather = weather;
    //    }
    //    @SerializedName("weather")
    //    private List<Weather> mWeather;
    @SerializedName("wind")
    var wind: Wind? = null
}
