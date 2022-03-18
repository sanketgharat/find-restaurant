package com.sg.findrestaurant.data.remote

import com.sg.findrestaurant.data.model.Restaurant
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query

interface ApiServiceInterface {

    @Headers("Accept: application/json")
    @GET("businesses/search")
    suspend fun getRestaurants(
        @Header("Authorization") token: String,
        @Query("term") term: String,
        @Query("location") location: String,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("radius") radius: Int,
        @Query("sort_by") sortBy: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): Restaurant

}