package com.sg.findrestaurant.data.model

import com.google.gson.annotations.SerializedName

data class Location(
    @SerializedName("city") var city: String? = null,
    @SerializedName("country") var country: String? = null,
    @SerializedName("address2") var address2: String? = null,
    @SerializedName("address3") var address3: String? = null,
    @SerializedName("state") var state: String? = null,
    @SerializedName("address1") var address1: String? = null,
    @SerializedName("zip_code") var zipCode: String? = null
)
