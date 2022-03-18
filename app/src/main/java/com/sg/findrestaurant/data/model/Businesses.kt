package com.sg.findrestaurant.data.model

import com.google.gson.annotations.SerializedName

data class Businesses(
    @SerializedName("rating") var rating: Double? = null,
    @SerializedName("price") var price: String? = null,
    @SerializedName("phone") var phone: String? = null,
    @SerializedName("id") var id: String? = null,
    @SerializedName("alias") var alias: String? = null,
    @SerializedName("is_closed") var isClosed: Boolean,
    @SerializedName("categories") var categories: List<Categories>?,
    @SerializedName("review_count") var reviewCount: Int? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("url") var url: String? = null,
    @SerializedName("coordinates") var coordinates: Coordinates? = Coordinates(),
    @SerializedName("image_url") var imageUrl: String? = null,
    @SerializedName("location") var location: Location? = Location(),
    @SerializedName("distance") var distance: Double,
    @SerializedName("transactions") var transactions: List<String>?
){
    constructor(): this(0.0, "", "", "", "", true, null, 0, "", "", Coordinates(), "", Location(), 0.0, null){

    }
}
