package com.brewkery.app.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL =
        "https://raw.githubusercontent.com/VivekShah138/Brewkery/main/"

    val api: BrewkeryApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BrewkeryApi::class.java)
    }
}