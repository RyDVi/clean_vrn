package com.rydvi.clean_vrn.ui.utils

import android.os.Build
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*

fun parseWithZero(value: Int): String {
    return if (value < 10) "0$value" else value.toString()
}

fun parseISODate(dateISO: String?): Date {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val timeFormatter = DateTimeFormatter.ISO_DATE_TIME
        val accessor = timeFormatter.parse(dateISO)
        return Date.from(Instant.from(accessor))
    } else {
        TODO("VERSION.SDK_INT < O")
    }
}

fun formatDateTime(date: Date): String = SimpleDateFormat("dd.MM.yyyy hh:mm").format(date)
