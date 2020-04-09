package com.rydvi.clean_vrn.ui.map

import android.content.Context
import androidx.annotation.DrawableRes
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.rydvi.clean_vrn.R
import com.rydvi.clean_vrn.ui.utils.bitmapDescriptorFromVector
import com.rydvi.clean_vrn.ui.utils.bitmapDescriptorFromVectorInMapMarker

class MarkerControl(map: GoogleMap, context: Context) {

    private val mMap = map
    private val context = context

    /**
     * Добавление маркера на карту
     */
    fun addMarker(location: LatLng, title: String, @DrawableRes drawableId: Int?): Marker {
        val addedMarker = mMap.addMarker(
            MarkerOptions().position(location).title(title).icon(
                drawableId?.let {
                    bitmapDescriptorFromVectorInMapMarker(context!!, drawableId)
                }?.also {
                    bitmapDescriptorFromVector(context!!, R.drawable.ic_map_pin_filled_blue_48dp)
                }
            ).draggable(true)
        )
        addedMarker.tag = 1
        return addedMarker
    }

    /**
     * Добавление маркера отходов
     */
    fun addGarbage(location: LatLng): Marker {
        return addMarker(location, "Garbage", R.drawable.ic_bag_icon)
    }

    /**
     * Добавление маркера туалета
     */
    fun addToilet(location: LatLng): Marker {
        return addMarker(location, "Toilet", R.drawable.ic_toilet)
    }

    /**
     * Добавление маркера с неизвестным местом
     */
    fun addAnotherPlace(location: LatLng): Marker {
        return addMarker(location, "Another place", null)
    }

    /**
     * Добавление маркера с точкой старта
     */
    fun addStartPlace(location: LatLng): Marker {
        return addMarker(location, "Start place", R.drawable.ic_start_flag)
    }
}