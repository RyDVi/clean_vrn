package com.rydvi.clean_vrn.api

import org.codehaus.jackson.annotate.JsonIgnoreProperties
import org.codehaus.jackson.annotate.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
class Place {
    @JsonProperty("id")
    var id: Long? = null
        get() = field
        set(value) {
            field = value
        }

    @JsonProperty("point")
    var point: LatLng? = null
        get() = field
        set(value) {
            field = value
        }

    @JsonProperty("polygon")
    var polygon: Array<LatLng>? = null
        get() = field
        set(value) {
            field = value
        }

    @JsonProperty("id_place_type")
    var placeType: Int? = null
        get() = field
        set(value) {
            field = value
        }

    @JsonProperty("description")
    var description: String? = null
        get() = field
        set(value) {
            field = value
        }

    fun getGooglePolygonPoints(): Array<com.google.android.gms.maps.model.LatLng>? {
        var googlePoints: Array<com.google.android.gms.maps.model.LatLng> = arrayOf()
        polygon?.let {
            for (coordinate in it) {
                googlePoints = googlePoints!!.plusElement(coordinate.toGoogleLatLng())
            }
        }
        return if (googlePoints.isNotEmpty()) googlePoints else null
    }
}