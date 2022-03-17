package com.sg.findrestaurant.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sg.findrestaurant.data.RestaurantRepository
import com.sg.findrestaurant.data.model.Restaurant
import com.sg.findrestaurant.data.remote.ApiResponse
import com.sg.findrestaurant.utils.Constants.API_TOKEN
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RestaurantViewModel @Inject constructor(
    private val repository: RestaurantRepository
) : ViewModel() {

    companion object {
        private const val TAG = "RestaurantViewModel"
    }

    private val _restaurantApiResult = MutableLiveData<ApiResponse>()
    val restaurantApiResult: LiveData<ApiResponse> get() = _restaurantApiResult

    private var restaurantApiJob: Job? = null

    fun getRestaurants(
        term: String = "restaurants",
        location: String = "NYC",
        radius: Int = 500,
        sortBy: String = "distance",
        limit: Int = 15
    ) {

        restaurantApiJob?.cancel()

        restaurantApiJob = viewModelScope.launch {
            _restaurantApiResult.postValue(ApiResponse.Loading)
            withContext(IO) {
                try {
                    val result = repository.getData(API_TOKEN, term, location, radius, sortBy, limit)
                    _restaurantApiResult.postValue(ApiResponse.Success<Restaurant>(result))
                } catch (throwable: Throwable) {
                    _restaurantApiResult.postValue(ApiResponse.Failure(throwable.message.toString()))
                }
            }
        }
    }


}