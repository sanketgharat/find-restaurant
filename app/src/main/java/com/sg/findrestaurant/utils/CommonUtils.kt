package com.sg.findrestaurant.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.sg.findrestaurant.utils.Constants.DATE_TIME_FORMAT
import java.lang.Exception
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object CommonUtils {
    private const val TAG = "CommonUtils"

    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    Log.d("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    Log.d("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                    Log.d("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }

    fun getDate(dateString: String): String {
        return try {
            val format1 = SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault())
            val date: Date? = format1.parse(dateString)
            val sdf: DateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
            if(date == null)
                return "date"
            sdf.format(date)
        } catch (ex: Exception) {
            ex.printStackTrace()
            "date"
        }
    }

    fun getTime(dateString: String): String {
        return try {
            val format1 = SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault())
            val date: Date? = format1.parse(dateString)
            val sdf: DateFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
            if(date == null)
                return "date"
            val netDate: Date = date
            sdf.format(netDate)
        } catch (ex: Exception) {
            ex.printStackTrace()
            "date"
        }
    }
}