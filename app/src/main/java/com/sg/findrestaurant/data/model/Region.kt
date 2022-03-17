package com.sg.findrestaurant.data.model

import com.google.gson.annotations.SerializedName
import com.sg.findrestaurant.data.model.Center

data class Region(
    @SerializedName("center") var center: Center
)
