package com.rydvi.clean_vrn.api

import com.google.android.gms.maps.model.LatLng
import org.codehaus.jackson.annotate.JsonProperty

class Place{
    @JsonProperty("id")
    var id: Long? = null
        get() = field
        set(value) {
            field = value
        }

    @JsonProperty("location")
    var location: LatLng? = null
        get() = field
        set(value) {
            field = value
        }

    @JsonProperty("type")
    var placeType: Long? = null
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
}