package com.brewkery.app.data.network

import com.brewkery.app.data.model.MenuItem
import com.brewkery.app.data.model.MenuResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface BrewkeryApi {
    @GET("data.json")
    suspend fun getMenu() : MenuResponse

    @GET("api/items/{id}.json")
    suspend fun getItem(
        @Path("id") id: Int
    ) : MenuItem
}