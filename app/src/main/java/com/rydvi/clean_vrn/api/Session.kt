package com.rydvi.clean_vrn.api

import org.codehaus.jackson.annotate.JsonProperty

class Session {
    @JsonProperty("session_id")
    var idSession: String? = null
        get() = field
        set(value) {
            field = value
        }

    @JsonProperty("id_user_type")
    var idUserType: Long? = null
        get() = field
        set(value) {
            field = value
        }

    @JsonProperty("id_user")
    var idUser:Int? = null
        get() = field
        set(value) {
            field = value
        }

    @JsonProperty("firstname")
    var firstname:String? = null
        get() = field
        set(value) {
            field = value
        }

    @JsonProperty("lastname")
    var lastname:String? = null
        get() = field
        set(value) {
            field = value
        }

    @JsonProperty("middlename")
    var middlename:String? = null
        get() = field
        set(value) {
            field = value
        }

    @JsonProperty("email")
    var email:String? = null
        get() = field
        set(value) {
            field = value
        }

    @JsonProperty("phone")
    var phone:String? = null
        get() = field
        set(value) {
            field = value
        }
}

