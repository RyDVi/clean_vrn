package com.rydvi.clean_vrn.ui.map

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.rydvi.clean_vrn.R
import com.rydvi.clean_vrn.ui.utils.bitmapDescriptorFromVector
import com.rydvi.clean_vrn.ui.utils.bitmapDescriptorFromVectorInMapMarker


class MapFragment : Fragment(), OnMapReadyCallback {

    private val VORONEZH_LOCATION = LatLng(51.668239, 39.192099)

    private lateinit var mMap: GoogleMap
    private lateinit var mMapView: MapView
    //    private lateinit var mSearchView: SearchView

    private lateinit var mBtnAddToilet: ImageButton
    private lateinit var mBtnAddAnotherPlace: ImageButton
    private lateinit var mBtnAddGarbageBag: ImageButton
    private lateinit var mBtnSetPlaceStart: ImageButton
    private lateinit var mBtnSetPlaceQuestZone: ImageButton

    private lateinit var mapViewModel: MapViewModel

    private var mapEditMode = MapEditMode.reading
    private var mapPlaceMode = MapPlaceMode.nothing

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

        _setupButtonsAndClickListeners(rootView)

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
                    MapEditMode.add -> {
                        when (mapPlaceMode) {
                            MapPlaceMode.toilet -> {
                                addToilet(clickLocation)
                            }
                            MapPlaceMode.garbagePlace -> {
                                addGarbage(clickLocation)
                            }
                            MapPlaceMode.anotherPlace -> {
                                addAnotherPlace(clickLocation)
                            }
                            MapPlaceMode.startPlace -> {
                                addStartPlace(clickLocation)
                            }
                        }
                    }
                }
                mapEditMode = MapEditMode.reading
            }

            mMap.setOnMarkerClickListener { marker ->
                _showMarkerDialogActions { dialogInterface, position, markerAction ->
                    when (markerAction) {
                        MarkerActions.rename -> {
                        }
                        MarkerActions.remove -> {
                        }
                        MarkerActions.move -> {
                        }
                    }
                }
                false
            }

            // Move camera to voronezh position
            mMap.moveCamera(CameraUpdateFactory.newLatLng(VORONEZH_LOCATION))
            mMap.animateCamera(CameraUpdateFactory.zoomTo(12.0f))
            mMap.uiSettings.isZoomControlsEnabled = true
            mMap.uiSettings.isMyLocationButtonEnabled = true
            mMapView.onResume()
        }
    }

    private fun addMarker(location: LatLng, title: String, @DrawableRes drawableId: Int?) {
        mMap.addMarker(
            MarkerOptions().position(location).title(title).icon(
                drawableId?.let {
                    bitmapDescriptorFromVectorInMapMarker(context!!, drawableId)
                }?.also {
                    bitmapDescriptorFromVector(context!!, R.drawable.ic_map_pin_filled_blue_48dp)
                }
            )
        )
    }

    private fun addGarbage(location: LatLng) {
        addMarker(location, "Garbage", R.drawable.ic_bag_icon)
    }


    private fun addToilet(location: LatLng) {
        addMarker(location, "Toilet", R.drawable.ic_toilet)
    }

    private fun addAnotherPlace(location: LatLng) {
        addMarker(location, "Another place", null)
    }

    private fun addStartPlace(location: LatLng) {
        addMarker(location, "Start place", R.drawable.ic_start_flag)
    }

    /**
     * Устанавливает указанный режим, если текущий режим не этот.
     * Если текущий режим = устанавливаемомум, то режим становится "чтение"
     */
    private fun _setMapEditModeIfNotThis(editMode: MapEditMode) {
        mapEditMode = if (mapEditMode !== editMode) {
            editMode
        } else {
            MapEditMode.reading
        }
    }

    /**
     * Установка кнопок и событий клика для них
     */
    private fun _setupButtonsAndClickListeners(rootView: View) {
        mBtnAddToilet = rootView.findViewById(R.id.btn_add_toilet)
        mBtnAddToilet.setOnClickListener {
            _setMapEditModeIfNotThis(MapEditMode.add)
            mapPlaceMode = MapPlaceMode.toilet
        }

        mBtnAddGarbageBag = rootView.findViewById(R.id.btn_add_garbage_bag)
        mBtnAddGarbageBag.setOnClickListener {
            _setMapEditModeIfNotThis(MapEditMode.add)
            mapPlaceMode = MapPlaceMode.garbagePlace
        }

        mBtnAddAnotherPlace = rootView.findViewById(R.id.btn_add_another_place)
        mBtnAddAnotherPlace.setOnClickListener {
            _setMapEditModeIfNotThis(MapEditMode.add)
            mapPlaceMode = MapPlaceMode.anotherPlace
        }

        mBtnSetPlaceStart = rootView.findViewById(R.id.btn_set_place_start)
        mBtnSetPlaceStart.setOnClickListener {
            _setMapEditModeIfNotThis(MapEditMode.add)
            mapPlaceMode = MapPlaceMode.startPlace
        }

        mBtnSetPlaceQuestZone = rootView.findViewById(R.id.btn_set_place_quest_zone)
        mBtnSetPlaceQuestZone.setOnClickListener {
            _setMapEditModeIfNotThis(MapEditMode.add)
            mapPlaceMode = MapPlaceMode.questZone
        }
    }

    fun _showMarkerDialogActions(callbackItemClicked: (DialogInterface, Int, MarkerActions) -> Unit) {
        val actions = arrayOf(
            resources.getString(R.string.dlg_marker_move),
            resources.getString(R.string.dlg_marker_remove),
            resources.getString(R.string.dlg_marker_rename)
        )
        val adapter =
            ArrayAdapter<String>(context!!, android.R.layout.select_dialog_item, actions)
        val builder = AlertDialog.Builder(context)
        builder.setTitle(resources.getString(R.string.dlg_marker_select_action))
        builder.setAdapter(adapter) { dialogInterface, position ->
            when (position) {
                0 -> callbackItemClicked(dialogInterface, position, MarkerActions.move)
                1 -> callbackItemClicked(dialogInterface, position, MarkerActions.remove)
                2 -> callbackItemClicked(dialogInterface, position, MarkerActions.rename)
            }

        }
        val dialog = builder.create()
        dialog.show()
    }

    enum class MapEditMode {
        reading, edit, add
    }

    enum class MapPlaceMode {
        nothing {
            override fun getPlaceId() = 0
        },
        anotherPlace {
            override fun getPlaceId() = 1
        },
        toilet {
            override fun getPlaceId() = 2
        },
        garbagePlace {
            override fun getPlaceId() = 3
        },
        startPlace {
            override fun getPlaceId() = 4
        },
        questZone {
            override fun getPlaceId() = 5
        };

        abstract fun getPlaceId(): Int
    }

    enum class MarkerActions {
        rename, remove, move
    }
}