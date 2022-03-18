package com.sg.findrestaurant.data

import com.sg.findrestaurant.data.remote.ApiServiceInterface

class RestaurantRepository(private val apiServiceInterface: ApiServiceInterface) {
    suspend fun getData(
        header: String, term: String, location: String, latitude: Double,
        longitude: Double, radius: Int, sortBy: String, limit: Int, offset: Int
    ) =
        apiServiceInterface.getRestaurants(header, term, location, latitude, longitude, radius, sortBy, limit, offset)
}