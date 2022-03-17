package com.sg.findrestaurant.data.model

import com.google.gson.annotations.SerializedName
import com.sg.findrestaurant.data.model.Businesses
import com.sg.findrestaurant.data.model.Region

data class Restaurant(
    @SerializedName("businesses") var businesses: List<Businesses>,
    @SerializedName("total") var total: Int? = null,
    @SerializedName("region") var region: Region
)
