package com.sg.findrestaurant.data

import com.sg.findrestaurant.data.remote.ApiServiceInterface

class RestaurantRepository(private val apiServiceInterface: ApiServiceInterface) {
    suspend fun getData(header : String,term: String, location: String, radius: Int, sortBy: String, limit: Int) =
        apiServiceInterface.getRestaurants(header,term, location, radius, sortBy, limit)
}