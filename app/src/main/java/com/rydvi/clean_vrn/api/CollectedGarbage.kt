package com.rydvi.clean_vrn.api

import org.codehaus.jackson.annotate.JsonProperty

class CollectedGarbage {
    @JsonProperty("id_collected_garbage")
    var id: Long? = null
        get() = field
        set(value) {
            field = value
        }

    @JsonProperty("id_team")
    var id_team: Long? = null
        get() = field
        set(value) {
            field = value
        }

    @JsonProperty("id_garbage")
    var id_garbage: Long? = null
        get() = field
        set(value) {
            field = value
        }

    @JsonProperty("garbage_name")
    var garbage_name:String? = null
        get() = field
        set(value) {
            field = value
        }

    var count:Int? = null
        get() = field
        set(value) {
            field = value
        }

    var coefficient:Int? = null
        get() = field
        set(value) {
            field = value
        }

    var sum_points:Int? = null
        get() = field
        set(value) {
            field = value
        }
}