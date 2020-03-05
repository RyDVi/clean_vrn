package com.rydvi.clean_vrn.api

import org.codehaus.jackson.annotate.JsonProperty
import org.codehaus.jackson.annotate.JsonValue

class Team{
    var id:Long? = null
        get() = field
        set(value) {
            field = value
        }

    var name:String? = null
        get() = field
        set(value) {
            field = value
        }

    var number:Int? = null
        get() = field
        set(value) {
            field = value
        }

    @JsonProperty("sum_points")
    var sumPoints:Int? = null
        get() = field
        set(value) {
            field = value
        }

}