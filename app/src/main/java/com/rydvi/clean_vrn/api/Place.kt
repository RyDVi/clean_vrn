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

    @JsonProperty("type")
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
}