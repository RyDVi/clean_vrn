package com.rydvi.clean_vrn.ui.map

import android.app.Activity
import android.content.Context
import androidx.annotation.DrawableRes
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.rydvi.clean_vrn.R
import com.rydvi.clean_vrn.ui.utils.bitmapDescriptorFromVector
import com.rydvi.clean_vrn.ui.utils.bitmapDescriptorFromVectorInMapMarker

class MarkerControl(map: GoogleMap, activity: Activity) {

    private val mMap = map
    private val activity = activity

    /**
     * Добавление маркера на карту
     */
    fun addMarker(
        location: LatLng,
        title: String, @DrawableRes drawableId: Int?,
        id: Long?,
        placeMode: MapPlaceMode,
        description: String?,
        afterAdd: (Marker) -> Unit
    ) {
        activity.runOnUiThread {
            val addedMarker = mMap.addMarker(
                MarkerOptions().position(location).title(title).icon(
                    drawableId?.let {
                        bitmapDescriptorFromVectorInMapMarker(activity!!, drawableId)
                    } ?: run {
                        bitmapDescriptorFromVector(
                            activity!!,
                            R.drawable.ic_map_pin_filled_blue_48dp
                        )
                    }
                ).draggable(true)
            )
            addedMarker.tag = MarkerUnique(id = id, placeMode = placeMode)
            addedMarker.snippet = if (description !== null) description else ""
            afterAdd(addedMarker)
        }
    }

    /**
     * Добавление маркера отходов
     */
    fun addGarbage(
        location: LatLng,
        id: Long?,
        description: String?,
        afterAdd: (Marker) -> Unit
    ) {
        addMarker(
            location,
            activity.resources.getString(R.string.map_marker_title_garbage),
            R.drawable.ic_bag_icon,
            id,
            MapPlaceMode.GarbagePlace,
            description,
            afterAdd
        )

    }

    /**
     * Добавление маркера туалета
     */
    fun addToilet(location: LatLng, id: Long?, description: String?, afterAdd: (Marker) -> Unit) {
        addMarker(
            location,
            activity.resources.getString(R.string.map_marker_title_toilet),
            R.drawable.ic_toilet,
            id,
            MapPlaceMode.Toilet,
            description,
            afterAdd
        )
    }

    /**
     * Добавление маркера с неизвестным местом
     */
    fun addAnotherPlace(
        location: LatLng,
        id: Long?,
        description: String?,
        afterAdd: (Marker) -> Unit
    ) {
        return addMarker(
            location,
            activity.resources.getString(R.string.map_marker_title_another_place),
            null,
            id,
            MapPlaceMode.AnotherPlace,
            description,
            afterAdd
        )
    }

    /**
     * Добавление маркера с точкой старта
     */
    fun addStartPlace(location: LatLng, id: Long?, description: String?, afterAdd: (Marker) -> Unit) {
        return addMarker(
            location,
            activity.resources.getString(R.string.map_marker_title_start_place),
            R.drawable.ic_start_flag,
            id,
            MapPlaceMode.StartPlace,
            description,
            afterAdd
        )
    }

    fun addMarkerByPlaceTypeID(
        location: LatLng,
        id: Long?,
        placeTypeId: Int,
        description: String?,
        afterAdd: (Marker) -> Unit
    ) =
        when (placeTypeId) {
            MapPlaceMode.StartPlace.getPlaceId() -> addStartPlace(location, id, description, afterAdd)
            MapPlaceMode.AnotherPlace.getPlaceId() -> addAnotherPlace(location, id, description, afterAdd)
            MapPlaceMode.GarbagePlace.getPlaceId() -> addGarbage(location, id, description, afterAdd)
            MapPlaceMode.Toilet.getPlaceId() -> addToilet(location, id, description, afterAdd)
            else -> null
        }

}