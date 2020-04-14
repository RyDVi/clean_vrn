package com.rydvi.clean_vrn.api

import com.google.android.gms.maps.model.LatLng
import org.codehaus.jackson.annotate.JsonProperty

class LatLng {
    @JsonProperty("latitude")
    var latitude: Double? = null
        get() = field
        set(value) {
            field = value
        }

    @JsonProperty("longitude")
    var longitude: Double? = null
        get() = field
        set(value) {
            field = value
        }

    fun parseGoogleLatLng(latLng: LatLng): com.rydvi.clean_vrn.api.LatLng {
        latitude = latLng.latitude
        longitude = latLng.longitude
        return this
    }

    fun toGoogleLatLng(): LatLng {
        return com.google.android.gms.maps.model.LatLng(latitude!!, longitude!!)
    }

}