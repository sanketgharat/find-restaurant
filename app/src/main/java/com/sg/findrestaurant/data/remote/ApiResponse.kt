package com.sg.findrestaurant.data.remote

sealed class ApiResponse {
    object Loading : ApiResponse()
    class Success<T>(val data: T) : ApiResponse()
    class Failure(val errorMessage: String) : ApiResponse()
}