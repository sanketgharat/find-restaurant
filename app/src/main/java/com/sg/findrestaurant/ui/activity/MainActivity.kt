package com.sg.findrestaurant.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.slider.Slider
import com.sg.findrestaurant.R
import com.sg.findrestaurant.data.model.Restaurant
import com.sg.findrestaurant.data.remote.ApiResponse
import com.sg.findrestaurant.databinding.ActivityMainBinding
import com.sg.findrestaurant.ui.adapter.RestaurantsListAdapter
import com.sg.findrestaurant.ui.viewmodel.RestaurantViewModel
import com.sg.findrestaurant.ui.viewmodel.ViewModelFactory
import com.sg.findrestaurant.utils.CommonUtils
import com.sg.findrestaurant.utils.Constants.DISTANCE_M_PER_KM
import dagger.android.AndroidInjection
import javax.inject.Inject

class MainActivity : BaseActivity() {

    companion object {
        private const val TAG = "MainActivityT"
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: RestaurantViewModel
    lateinit var adapterRestaurants: RestaurantsListAdapter
    private var radius = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AndroidInjection.inject(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this, viewModelFactory)[RestaurantViewModel::class.java]
        radius = resources.getInteger(R.integer.default_radius_value_meters)
        initViews()
        initObservers()
        loadData()
    }

    override fun initViews() {
        initRecycler()
        binding.swipeRefreshLayout.setOnRefreshListener {
            Log.d(TAG, "initViews: setOnRefreshListener")
            if (CommonUtils.isOnline(applicationContext)) {
                loadData()
            } else {
                showToast(getString(R.string.check_internet_message))
            }
            binding.swipeRefreshLayout.isRefreshing = false
        }
        initSliderListener()

    }

    private fun initRecycler() {
        adapterRestaurants = RestaurantsListAdapter()
        val layoutManager = LinearLayoutManager(applicationContext)
        binding.recyclerRestaurants.setHasFixedSize(true)
        binding.recyclerRestaurants.layoutManager = layoutManager
        binding.recyclerRestaurants.itemAnimator = DefaultItemAnimator()
        binding.recyclerRestaurants.adapter = adapterRestaurants
    }

    private fun initSliderListener(){
        binding.slider.addOnSliderTouchListener(object : Slider.OnSliderTouchListener{
            override fun onStartTrackingTouch(slider: Slider) {
                Log.d(TAG, "onStartTrackingTouch : ${slider.value} M")
            }

            override fun onStopTrackingTouch(slider: Slider) {
                Log.d(TAG, "onStopTrackingTouch : ${slider.value} M")

                radius = slider.value.toInt()

                val valueRadius = if (radius >= DISTANCE_M_PER_KM) {
                    getString(R.string.radius_value_km, radius / DISTANCE_M_PER_KM)
                } else {
                    getString(R.string.radius_value_m, radius)
                }

                binding.textRadiusSelectorValue.text = valueRadius

                loadData()
            }

        })
    }

    override fun initObservers() {
        viewModel.restaurantApiResult.observe(this, {
            Log.d(TAG, "initObservers: restaurantApiResult")

            when (it) {
                is ApiResponse.Loading -> showLoading()
                is ApiResponse.Success<*> -> showData(it.data as Restaurant)
                is ApiResponse.Failure -> showError(it.errorMessage)
            }
        })
    }

    private fun showLoading() {
        Log.d(TAG, "showLoading:")
        binding.swipeRefreshLayout.isRefreshing = true
    }

    private fun showError(errorMessage: String) {
        Log.d(TAG, "errorMessage: $errorMessage")
        binding.swipeRefreshLayout.isRefreshing = false
        //showToast(errorMessage)
    }

    private fun showData(restaurant: Restaurant) {
        Log.d(TAG, "showData: ${restaurant.businesses.size}")
        binding.swipeRefreshLayout.isRefreshing = false
        adapterRestaurants.setList(restaurant.businesses)
    }


    override fun loadData() {
        if (CommonUtils.isOnline(applicationContext))
            viewModel.getRestaurants(radius = radius)
        else
            showToast(getString(R.string.check_internet_message))
    }


}