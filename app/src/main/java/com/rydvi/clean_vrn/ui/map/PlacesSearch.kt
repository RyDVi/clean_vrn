package com.rydvi.clean_vrn.ui.map

import android.content.Context
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.widget.SearchView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import java.io.IOException

class PlacesSearch(searchView: SearchView, map: GoogleMap, context: Context) :
    SearchView.OnQueryTextListener {
    private val mSearchView = searchView
    private val mContext = context
    private val mMap = map

    override fun onQueryTextSubmit(newText: String?): Boolean {
        val location = mSearchView.query.toString()
        var addressList: List<Address>? = null
        if (location != null && location != "") {
            val geocoder = Geocoder(mContext)
            try {
                addressList = geocoder.getFromLocationName(location, 1)
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
            addressList?.let { addressList ->
                val address = addressList[0]
                val latLng = LatLng(address.latitude, address.longitude)
//                mMap.addMarker(MarkerOptions().position(latLng).title(location))
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10F))
            }
        }
        return false
    }

    override fun onQueryTextChange(query: String?): Boolean {
        val location = mSearchView.query.toString()
        var addressList: List<Address>? = null
        if (location != null && location != "") {
            val geocoder = Geocoder(mContext)
            try {
                addressList = geocoder.getFromLocationName(location, 1)
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
            addressList?.let { addressList ->
                if (addressList.isNotEmpty()) {
                    val address = addressList[0]

                    mSearchView.suggestionsAdapter =
                        MapSearchListAdapter(mContext, null, mSearchView)
                }
            }
        }
        return false
    }
}