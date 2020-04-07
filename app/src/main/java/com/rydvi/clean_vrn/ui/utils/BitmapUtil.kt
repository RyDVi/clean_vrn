package com.rydvi.clean_vrn.ui.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.rydvi.clean_vrn.R

fun bitmapDescriptorFromVector(context: Context, @DrawableRes resId: Int): BitmapDescriptor? {
    val drawable = ContextCompat.getDrawable(context, resId)
    drawable!!.setBounds(
        0,
        0,
        drawable.intrinsicWidth,
        drawable.intrinsicHeight
    )
    val bitmap = Bitmap.createBitmap(
        drawable.intrinsicWidth,
        drawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    drawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

fun bitmapDescriptorFromVectorInMapMarker(context: Context, @DrawableRes vectorDrawableResourceId: Int): BitmapDescriptor? {
    val background =
        ContextCompat.getDrawable(context, R.drawable.ic_map_pin_filled_blue_48dp)
    background!!.setBounds(0, 0, background.intrinsicWidth, background.intrinsicHeight)
    val vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId)
    vectorDrawable!!.setBounds(
        40,
        20,
        vectorDrawable.intrinsicWidth + 40,
        vectorDrawable.intrinsicHeight + 20
    )
    val bitmap = Bitmap.createBitmap(
        background.intrinsicWidth,
        background.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    background.draw(canvas)
    vectorDrawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}