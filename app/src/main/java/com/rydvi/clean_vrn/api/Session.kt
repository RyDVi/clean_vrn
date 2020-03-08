package com.rydvi.clean_vrn.api

import org.codehaus.jackson.annotate.JsonProperty
import com.rydvi.clean_vrn.api.UserType as UserType1

class Session {
    @JsonProperty("session_id")
    var idSession: String? = null
        get() = field
        set(value) {
            field = value
        }

    @JsonProperty("id_user_type")
    var idUserType: Int? = null
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
}

enum class UserType {
    administrator {
        override fun getUserType() = administrator
        override fun getUserTypeId() = 1
    },
    organizator{
        override fun getUserType() = organizator
        override fun getUserTypeId() = 2
    };
    abstract fun getUserType(): UserType1
    abstract fun getUserTypeId(): Int
}

