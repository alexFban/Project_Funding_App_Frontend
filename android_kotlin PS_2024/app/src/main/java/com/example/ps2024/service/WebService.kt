package com.example.ps2024.service

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Configuration for API usage
 */
object WebService {
    // target URL
    private const val API_URL = "https://api.openweathermap.org"
    private var webApiService: WebApiService? = null
    @JvmStatic
    val instance: WebApiService?
        /**
         * API object which can be used in order to perform any call based on the
         * definition done within [WebApiService] interface
         * @return single tone instance of service used for API
         */
        get() {
            if (webApiService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(provideOkHttp())
                    .build()
                webApiService = retrofit.create(WebApiService::class.java)
            }
            return webApiService
        }

    /**
     * Object used to perform HTTP calls
     * @return valid instance
     */
    private fun provideOkHttp(): OkHttpClient {
        val httpBuilder = OkHttpClient.Builder()
        httpBuilder.connectTimeout(30, TimeUnit.SECONDS)
        return httpBuilder.build()
    }
}
