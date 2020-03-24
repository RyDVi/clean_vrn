package com.rydvi.clean_vrn.api

import org.codehaus.jackson.annotate.JsonProperty

class Coefficient {
    var id: Long? = null
        get() = field
        set(value) {
            field = value
        }

    @JsonProperty("id_garbage")
    var idGarbage:Long? = null
        get() = field
        set(value) {
            field = value
        }

    @JsonProperty("id_game")
    var idGame:Long? = null
        get() = field
        set(value) {
            field = value
        }

    var coefficient:Int? = null
        get() = field
        set(value) {
            field = value
        }

    var name:String? = null
        get() = field
        set(value) {
            field = value
        }
}