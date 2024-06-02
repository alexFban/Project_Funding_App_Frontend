package com.example.ps2024.service

import com.example.ps2024.model.Weather
import retrofit2.Call
import retrofit2.http.GET

/**
 * References for available APIs for usage
 */
interface WebApiService {

    @get:GET("/data/2.5/weather?q=Cluj-Napoca&appid=ea1863083f37f636a3d408f54bc64c79")
    val weather: Call<Weather?>?
}
