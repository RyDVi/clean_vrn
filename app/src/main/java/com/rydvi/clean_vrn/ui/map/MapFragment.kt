package com.rydvi.clean_vrn.ui.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.rydvi.clean_vrn.R
import com.rydvi.clean_vrn.ui.utils.bitmapDescriptorFromVectorInMapMarker


class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mapViewModel: MapViewModel
    private lateinit var mMap: GoogleMap
    private lateinit var mMapView: MapView
    //    private lateinit var mSearchView: SearchView

    private var mapEditMode = MapEditMode.reading

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_map, container, false)

        mapViewModel =
            ViewModelProviders.of(this).get(MapViewModel::class.java)
        mMapView = rootView.findViewById(R.id.mapView)


//        mSearchView = rootView.findViewById(R.id.search_location)
//        mSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(newText: String?): Boolean {
//                val location = mSearchView.query.toString()
//                var addressList: List<Address>? = null
//                if (location != null && location != "") {
//                    val geocoder = Geocoder(activity)
//                    try {
//                        addressList = geocoder.getFromLocationName(location, 1)
//                    } catch (ex: IOException) {
//                        ex.printStackTrace()
//                    }
//                    addressList?.let { addressList ->
//                        val address = addressList[0]
//                        val latLng = LatLng(address.latitude, address.longitude)
//                        mMap.addMarker(MarkerOptions().position(latLng).title(location))
//                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10F))
//                    }
//                }
//                return false
//            }
//
//            override fun onQueryTextChange(query: String?): Boolean {
//                val location = mSearchView.query.toString()
//                var addressList: List<Address>? = null
//                if (location != null && location != "") {
//                    val geocoder = Geocoder(activity)
//                    try {
//                        addressList = geocoder.getFromLocationName(location, 1)
//                    } catch (ex: IOException) {
//                        ex.printStackTrace()
//                    }
//                    addressList?.let { addressList ->
//                        val address = addressList[0]
//
//                        mSearchView.suggestionsAdapter =
//                            MapSearchListAdapter(activity!!, null, mSearchView)
//                    }
//                }
//                return false
//            }
//        })


        val btnAddPlace = rootView.findViewById<ImageButton>(R.id.btn_add_place)
        btnAddPlace.setOnClickListener {
            mapEditMode = if (mapEditMode === MapEditMode.addPlace) {
                MapEditMode.reading
            } else {
                MapEditMode.addPlace
            }

        }

        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mMapView?.let {
            mMapView.onCreate(savedInstanceState)
            mMapView.onResume()
            mMapView.getMapAsync(this)
        }

        val autocompleteFragment =
            fragmentManager!!.findFragmentById(R.id.autocomplete_fragment)

//        (autocompleteFragment as PlaceAutocompleteFragment).setOnPlaceSelectedListener(object :
//            PlaceSelectionListener {
//            override fun onPlaceSelected(place: Place) {
//                txtVw.text = place.name
//            }
//
//            override fun onError(status: Status) {
//                txtVw.text = status.toString()
//            }
//        })
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        googleMap?.let {
            mMap = googleMap
            mMap.setOnMapClickListener { clickLocation ->
                when (mapEditMode) {
                    MapEditMode.addPlace -> {
                        mMap.addMarker(
                            MarkerOptions().position(clickLocation).title("Toilet").icon(
                                bitmapDescriptorFromVectorInMapMarker(
                                    context!!,
                                    R.drawable.ic_toilet
                                )
                            )
                        )
                    }
                }
                mapEditMode = MapEditMode.reading
            }

            // Add a marker in Sydney and move the camera
            val voronezh = LatLng(51.39396, 39.12037)
//            mMap.addMarker(MarkerOptions().position(voronezh).title("Is voronezh"))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(voronezh))
            mMapView.onResume()
        }
    }


    enum class MapEditMode {
        reading {
            override fun getModeId() = 1
        },
        addPlace {
            override fun getModeId() = 2
        };

        abstract fun getModeId(): Int
    }
}