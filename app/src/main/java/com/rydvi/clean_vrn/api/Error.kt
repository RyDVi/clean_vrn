package com.rydvi.clean_vrn.api

import org.codehaus.jackson.annotate.JsonIgnoreProperties
import org.codehaus.jackson.annotate.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
class Error {
    @JsonProperty("msg")
    var msg: String? = null
        get() = field
        set(value) {
            field = value
        }

    //Внутренний код ошибки разработанного API
    @JsonProperty("code")
    var code: Int? = null
        get() = field
        set(value) {
            field = value
        }
}