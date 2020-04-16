package com.rydvi.clean_vrn.api

import com.rydvi.clean_vrn.ui.utils.parseDate
import java.util.*

class Game {
    var id: Long? = null
        get() = field
        set(value) {
            field = value
        }

    var id_status: Long? = null
        get() = field
        set(value) {
            field = value
        }

    var name: String? = null
        get() = field
        set(value) {
            field = value
        }

    var description: String? = null
        get() = field
        set(value) {
            field = value
        }

    var route: String? = null
        get() = field
        set(value) {
            field = value
        }

    var datetime: String? = null
        get() = field
        set(value) {
            field = value
        }

    fun datetimeInDate(): Date? = parseDate(datetime)

}