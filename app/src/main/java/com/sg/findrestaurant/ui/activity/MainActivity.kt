package com.sg.findrestaurant.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.google.android.material.slider.Slider
import com.sg.findrestaurant.R
import com.sg.findrestaurant.data.model.Restaurant
import com.sg.findrestaurant.data.remote.ApiResponse
import com.sg.findrestaurant.databinding.ActivityMainBinding
import com.sg.findrestaurant.ui.adapter.RestaurantsListAdapter
import com.sg.findrestaurant.ui.viewmodel.RestaurantViewModel
import com.sg.findrestaurant.ui.viewmodel.ViewModelFactory
import com.sg.findrestaurant.utils.CommonUtils
import com.sg.findrestaurant.utils.Constants
import com.sg.findrestaurant.utils.Constants.DISTANCE_M_PER_KM
import com.sg.findrestaurant.utils.PaginationScrollListener
import dagger.android.AndroidInjection
import javax.inject.Inject
import kotlin.math.roundToInt


class MainActivity : BaseActivity() {

    companion object {
        private const val TAG = "MainActivityT"
        private const val REQ_CODE_LOCATION = 123
        private const val PAGE_START = 1
        private const val PAGE_SIZE = 15 //items per page
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: RestaurantViewModel
    lateinit var adapterRestaurants: RestaurantsListAdapter
    private var radius = 500
    private lateinit var locationRequest: LocationRequest
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    private var isLoading = false
    private var isLastPage = false
    private var isFirstTimeLoad = true
    private var totalPages = 0
    private var currentPage = PAGE_START

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.sg.findrestaurant.R.layout.activity_main)

        AndroidInjection.inject(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this, viewModelFactory)[RestaurantViewModel::class.java]
        radius = resources.getInteger(R.integer.default_radius_value_meters)
        initClasses()
        initViews()
        initObservers()
        //loadData()
        checkLocationPermission()
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

    override fun initClasses() {
        locationRequest = LocationRequest.create();
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY;
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 2000
    }

    private fun initRecycler() {
        adapterRestaurants = RestaurantsListAdapter()
        val layoutManager = LinearLayoutManager(applicationContext)
        binding.recyclerRestaurants.setHasFixedSize(true)
        binding.recyclerRestaurants.layoutManager = layoutManager
        binding.recyclerRestaurants.itemAnimator = DefaultItemAnimator()
        binding.recyclerRestaurants.adapter = adapterRestaurants

        binding.recyclerRestaurants.addOnScrollListener(object :
            PaginationScrollListener(layoutManager) {
            override fun loadMoreItems() {
                Log.d(
                    TAG,
                    "loadMoreItems : current:" + currentPage + " next:" + (currentPage + 1)
                )
                this@MainActivity.isLoading = true
                currentPage += 1

                // next page API call
                loadRestaurants(currentPage)
            }

            override val totalPageCountA: Int
                get() = totalPages
            override val isLastPageA: Boolean
                get() = isLastPage
            override val isLoadingA: Boolean
                get() = isLoading

        })
    }

    private fun initSliderListener() {
        binding.slider.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {
                Log.d(TAG, "onStartTrackingTouch : ${slider.value} M")
            }

            override fun onStopTrackingTouch(slider: Slider) {
                Log.d(TAG, "onStopTrackingTouch : ${slider.value} M")
                Log.d(TAG, "onStopTrackingTouch : ${slider.value / DISTANCE_M_PER_KM} KM")

                radius = slider.value.toInt()


                val valueRadius = if (radius >= DISTANCE_M_PER_KM) {
                    val radiusString = String.format("%.1f", slider.value / DISTANCE_M_PER_KM)
                    Log.d(TAG, "onStopTrackingTouch radiusString : $radiusString")
                    getString(R.string.radius_value_km, radiusString)
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
        if(currentPage == 1)
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
        binding.textNoData.visibility = View.GONE
        /*if(restaurant.businesses.isEmpty()){
            binding.textNoData.visibility = View.VISIBLE
        }else{
            binding.textNoData.visibility = View.GONE
        }

        adapterRestaurants.setList(restaurant.businesses)*/

        if (isFirstTimeLoad) {
            Log.d(
                TAG,
                "loadFirstPage"
            )
            isFirstTimeLoad = false

            isLoading = false
            isLastPage = false
            currentPage = PAGE_START
            Log.d(TAG, "restaurant.total: ${restaurant.total}")

            totalPages = if (restaurant.total != null && restaurant.total!! > 0)
                (restaurant.total!! + PAGE_SIZE - 1) / PAGE_SIZE
            else
                1

            Log.d(TAG, "TOTAL_PAGES: $totalPages")

            if (restaurant.businesses.isNotEmpty()) {
                adapterRestaurants.setList(restaurant.businesses)
                if (currentPage < totalPages) adapterRestaurants.addLoadingFooter()
                else isLastPage = true
            } else {
                binding.textNoData.visibility = View.VISIBLE
                adapterRestaurants.setList(emptyList())
            }


        } else {
            Log.d(TAG, "loadNextPage: $currentPage")
            adapterRestaurants.removeLoadingFooter()
            isLoading = false

            adapterRestaurants.addAll(restaurant.businesses)
            if (currentPage != totalPages) adapterRestaurants.addLoadingFooter()
            else isLastPage = true

        }
    }


    override fun loadData() {
        Log.d(TAG, "loadData: ")
        isFirstTimeLoad = true
        loadRestaurants()
    }

    private fun loadRestaurants(pageNumber: Int = 1) {
        showLoaderFetchingLocation(false)

        if (CommonUtils.isOnline(applicationContext)) {
            //adapterRestaurants.setList(emptyList())
            binding.textNoData.visibility = View.GONE

            if (pageNumber <= 1) {
                isFirstTimeLoad = true
                viewModel.getRestaurants(
                    radius = radius,
                    location = Constants.DEFAULT_LOCATION,
                    latitude = latitude,
                    longitude = longitude,
                    limit = PAGE_SIZE
                )
            } else {
                Log.d(TAG, "offset: ${(pageNumber - 1) * PAGE_SIZE}")
                viewModel.getRestaurants(
                    radius = radius,
                    location = Constants.DEFAULT_LOCATION,
                    latitude = latitude,
                    longitude = longitude,
                    limit = PAGE_SIZE,
                    offset = (pageNumber - 1) * PAGE_SIZE
                )
            }
        } else {
            binding.textNoData.visibility = View.VISIBLE
            showToast(getString(R.string.check_internet_message))
        }
    }

    private fun checkLocationPermission() {
        Log.d(TAG, "checkLocationPermission: ")

        when {
            ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.d(TAG, "PERMISSION_GRANTED: already")
                checkGpsEnabled()
            }
            shouldShowRequestPermissionRationale("Location p") -> {}
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                Log.d(TAG, "requestPermissionLauncher launch")
                requestPermissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            }
        }
    }

    private fun checkGpsEnabled() {
        Log.d(TAG, "checkGpsEnabled")
        if (isGPSEnabled()) {
            Log.d(TAG, "isGPSEnabled true")
            getCurrentLocation()
        } else {
            Log.d(TAG, "isGPSEnabled false")
            turnOnGPS()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {

        Log.d(TAG, "getCurrentLocation")
        LocationServices.getFusedLocationProviderClient(this@MainActivity)
            .requestLocationUpdates(locationRequest, object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    LocationServices.getFusedLocationProviderClient(this@MainActivity)
                        .removeLocationUpdates(this)
                    if (locationResult.locations.size > 0) {
                        val index = locationResult.locations.size - 1
                        val curLatitude = locationResult.locations[index].latitude
                        val curLongitude = locationResult.locations[index].longitude

                        latitude = curLatitude
                        longitude = curLongitude

                        Log.d(TAG, "locationResult: Latitude: $latitude - Longitude: $longitude")
                        loadData()
                    } else {
                        loadData()
                    }
                }
            }, Looper.getMainLooper())

    }

    private fun turnOnGPS() {
        Log.d(TAG, "turnOnGPS")
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)

        val result: Task<LocationSettingsResponse> = LocationServices.getSettingsClient(
            applicationContext
        )
            .checkLocationSettings(builder.build())

        result.addOnCompleteListener { task ->
            Log.d(TAG, "turnOnGPS result.addOnCompleteListener")
            try {
                val response = task.getResult(ApiException::class.java)
                Toast.makeText(this@MainActivity, "GPS is already turned on", Toast.LENGTH_SHORT)
                    .show()

            } catch (e: ApiException) {
                when (e.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        val resolvableApiException = e as ResolvableApiException
                        resolvableApiException.startResolutionForResult(
                            this@MainActivity,
                            REQ_CODE_LOCATION
                        )
                    } catch (ex: SendIntentException) {
                        ex.printStackTrace()
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {}
                }
            }
        }

    }


    private fun isGPSEnabled(): Boolean {
        Log.d(TAG, "isGPSEnabled")
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.d(TAG, "requestPermissionLauncher isGranted true")
                checkGpsEnabled()
            } else {
                Log.d(TAG, "requestPermissionLauncher isGranted false")
                showToast(getString(R.string.location_permission_denied_message))
                loadData()
            }
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQ_CODE_LOCATION) {
            if (resultCode == Activity.RESULT_OK) {
                getCurrentLocation()
            } else {
                loadData()
            }
        }
    }

    /*private fun showDialogFetchingLocation(){
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        dialogBuilder.setView(R.layout.loader_dialog_layout)
    }*/

    private fun showLoaderFetchingLocation(show: Boolean) {
        if (show) {
            binding.groupLoader.visibility = View.VISIBLE
            binding.groupData.visibility = View.GONE
        } else {
            binding.groupLoader.visibility = View.GONE
            binding.groupData.visibility = View.VISIBLE
        }
    }


}
