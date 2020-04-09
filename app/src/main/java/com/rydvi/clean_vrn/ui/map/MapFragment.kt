package com.rydvi.clean_vrn.ui.map

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.rydvi.clean_vrn.R
import com.rydvi.clean_vrn.api.Place
import com.rydvi.clean_vrn.ui.dialog.Dialog


class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
    GoogleMap.OnMapClickListener, GoogleMap.OnMarkerDragListener {

    private val VORONEZH_LOCATION = LatLng(51.668239, 39.192099)

    private lateinit var mMapView: MapView

    private lateinit var mPanelMarkers: LinearLayout
    private lateinit var mBtnAddToilet: ImageButton
    private lateinit var mBtnAddAnotherPlace: ImageButton
    private lateinit var mBtnAddGarbageBag: ImageButton
    private lateinit var mBtnSetPlaceStart: ImageButton
    private lateinit var mBtnSetPlaceQuestZone: ImageButton

    private lateinit var mPanelAcceptButtons: LinearLayout
    private lateinit var mBtnAccept: FloatingActionButton
    private lateinit var mBtnCancel: FloatingActionButton

    private lateinit var mPanelMarkerControl: LinearLayout
    private lateinit var mBtnControlEdit: ImageButton
    private lateinit var mBtnControlRemove: ImageButton

    private lateinit var mapViewModel: MapViewModel

    private var mapEditMode = MapEditMode.Reading
    private var mapPlaceMode = MapPlaceMode.Nothing
    private var markerActionMode = MarkerActions.Nothing
    private lateinit var currentMarker: Marker
    private lateinit var lastLocation: LatLng

    private lateinit var markerControl: MarkerControl

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_map, container, false)

        mapViewModel =
            ViewModelProviders.of(this).get(MapViewModel::class.java)
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

//        val autocompleteFragment =
//            fragmentManager!!.findFragmentById(R.id.autocomplete_fragment)
//
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
        googleMap?.let { map ->
            markerControl = MarkerControl(map, context!!)
            map.setOnMapClickListener(this)
            map.setOnMarkerClickListener(this)
            map.setOnMarkerDragListener(this)

            // Move camera to voronezh position
            map.moveCamera(CameraUpdateFactory.newLatLng(VORONEZH_LOCATION))
            map.animateCamera(CameraUpdateFactory.zoomTo(12.0f))
            map.uiSettings.isZoomControlsEnabled = true
            map.uiSettings.isMyLocationButtonEnabled = true
            mMapView.onResume()
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        currentMarker = marker
        markerActionMode = MarkerActions.Nothing
        mapEditMode = MapEditMode.Edit
        toggleButtons()

        return false
    }

    override fun onMapClick(clickLocation: LatLng) {
        when (mapEditMode) {
            MapEditMode.Add -> {
                when (mapPlaceMode) {
                    MapPlaceMode.Toilet -> {
                        markerControl.addToilet(clickLocation)
                        setModeOnlyReading()
                    }
                    MapPlaceMode.GarbagePlace -> {
                        markerControl.addGarbage(clickLocation)
                        setModeOnlyReading()
                    }
                    MapPlaceMode.AnotherPlace -> {
                        markerControl.addAnotherPlace(clickLocation)
                        setModeOnlyReading()
                    }
                    MapPlaceMode.StartPlace -> {
                        markerControl.addStartPlace(clickLocation)
                        setModeOnlyReading()
                    }
                    MapPlaceMode.QuestZone -> {
                        //TODO
                    }
                }
            }
            MapEditMode.Edit -> {
                setModeOnlyReading()
            }
        }
        toggleButtons()
    }

    override fun onMarkerDragEnd(marker: Marker) {
        Dialog(activity!!).showDialogAcceptCancel(
            {
                setModeOnlyReading()
                toggleButtons()
            },
            {
                currentMarker.position = lastLocation
                setModeOnlyReading()
                toggleButtons()
            },
            resources.getString(R.string.dlg_place_move_accept_msg),
            resources.getString(R.string.dlg_place_move_accept_accept),
            resources.getString(R.string.dlg_place_move_accept_cancel)
        )

    }

    override fun onMarkerDragStart(marker: Marker) {
        currentMarker = marker
        lastLocation = marker.position
        mapEditMode = MapEditMode.Edit
        markerActionMode = MarkerActions.Move
        toggleButtons()
    }

    override fun onMarkerDrag(marker: Marker) {

    }

    fun toggleButtons() {
        when {
            mapEditMode === MapEditMode.Reading -> {
                //Если режим чтения карты, то отображаем возможность для добавления элементов
                mPanelMarkers.visibility = LinearLayout.VISIBLE
                mPanelAcceptButtons.visibility = LinearLayout.GONE
                mPanelMarkerControl.visibility = LinearLayout.GONE
            }
            mapEditMode === MapEditMode.Add -> {
                //Если режим редактирования карты, то отображаем возможность отмены
                // выбора текущего маркера и
                mBtnAccept.hide()
                mPanelAcceptButtons.visibility = LinearLayout.VISIBLE
                mPanelMarkers.visibility = LinearLayout.GONE
                mPanelMarkerControl.visibility = LinearLayout.GONE
            }
            mapEditMode === MapEditMode.Edit -> {
                when (markerActionMode) {
                    MarkerActions.Move -> {
                        mPanelMarkerControl.visibility = LinearLayout.GONE
                        mPanelAcceptButtons.visibility = LinearLayout.GONE
                        mPanelMarkers.visibility = LinearLayout.GONE
                    }
                    else -> {
                        mBtnAccept.hide()
                        mPanelMarkerControl.visibility = LinearLayout.VISIBLE
                        mPanelAcceptButtons.visibility = LinearLayout.VISIBLE
                        mPanelMarkers.visibility = LinearLayout.GONE
                    }
                }
            }
        }
    }

    fun setModeOnlyReading() {
        mapEditMode = MapEditMode.Reading
        mapPlaceMode = MapPlaceMode.Nothing
        markerActionMode = MarkerActions.Nothing
    }

    /**
     * Устанавливает указанный режим, если текущий режим не этот.
     * Если текущий режим = устанавливаемомум, то режим становится "чтение"
     */
    private fun _setMapEditModeIfNotThis(editMode: MapEditMode) {
        mapEditMode = if (mapEditMode !== editMode) {
            editMode
        } else {
            MapEditMode.Reading
        }
    }

    /**
     * Установка кнопок и событий клика для них
     */
    private fun _setupButtonsAndClickListeners(rootView: View) {
        mMapView = rootView.findViewById(R.id.mapView)

        mBtnAddToilet = rootView.findViewById(R.id.btn_add_toilet)
        mBtnAddToilet.setOnClickListener {
            mapPlaceMode = MapPlaceMode.Toilet
            _onClickedButttonFromPanel(it as ImageButton)
        }

        mBtnAddGarbageBag = rootView.findViewById(R.id.btn_add_garbage_bag)
        mBtnAddGarbageBag.setOnClickListener {
            mapPlaceMode = MapPlaceMode.GarbagePlace
            _onClickedButttonFromPanel(it as ImageButton)
        }

        mBtnAddAnotherPlace = rootView.findViewById(R.id.btn_add_another_place)
        mBtnAddAnotherPlace.setOnClickListener {
            mapPlaceMode = MapPlaceMode.AnotherPlace
            _onClickedButttonFromPanel(it as ImageButton)
        }

        mBtnSetPlaceStart = rootView.findViewById(R.id.btn_set_place_start)
        mBtnSetPlaceStart.setOnClickListener {
            mapPlaceMode = MapPlaceMode.StartPlace
            _onClickedButttonFromPanel(it as ImageButton)
        }

        mBtnSetPlaceQuestZone = rootView.findViewById(R.id.btn_set_place_quest_zone)
        mBtnSetPlaceQuestZone.setOnClickListener {
            mapPlaceMode = MapPlaceMode.QuestZone
            _onClickedButttonFromPanel(it as ImageButton)
        }

        mPanelMarkers = rootView.findViewById(R.id.panel_markers)

        mBtnAccept = rootView.findViewById(R.id.btn_accept)
        mBtnAccept.setOnClickListener {
            currentMarker?.let {
                when (markerActionMode) {
                    MarkerActions.Move -> {
                        mapViewModel.updatePlace(Place().apply {
                            id = it.tag as Long
                            location = it.position
                        }, {
                            currentMarker.isDraggable = false
                            toggleButtons()
                        }, {
                            toggleButtons()
                            currentMarker.remove()
                        })
                    }
                }
            }
            MarkerActions.Nothing
        }

        mBtnCancel = rootView.findViewById(R.id.btn_cancel)
        mBtnCancel.setOnClickListener {
            currentMarker?.hideInfoWindow()
            setModeOnlyReading()
            toggleButtons()
        }

        mPanelAcceptButtons = rootView.findViewById(R.id.panel_accept_buttons)

        mBtnControlEdit = rootView.findViewById(R.id.btn_marker_control_edit)
        mBtnControlEdit.setOnClickListener {
            mapEditMode = MapEditMode.Edit
            markerActionMode = MarkerActions.Rename
            toggleButtons()
            val descriptionInput = EditText(activity!!)
            descriptionInput.inputType = InputType.TYPE_TEXT_FLAG_MULTI_LINE
            Dialog(activity!!).showDialogAcceptCancelWithContent(
                {
                    currentMarker.snippet = descriptionInput.text.toString()
                    currentMarker.hideInfoWindow()
                    setModeOnlyReading()
                    toggleButtons()
                },
                null,
                resources.getString(R.string.dlg_marker_rename_title),
                resources.getString(R.string.dlg_marker_rename_accept),
                resources.getString(R.string.dlg_marker_rename_cancel),
                descriptionInput
            )
        }

        mBtnControlRemove = rootView.findViewById(R.id.btn_marker_control_remove)
        mBtnControlRemove.setOnClickListener {
            Dialog(activity!!).showDialogAcceptCancel(
                {
                    mapViewModel.deletePlace(
                        Place(),
                        {
                            currentMarker.remove()
                            setModeOnlyReading()
                            toggleButtons()
                        },
                        {
                            setModeOnlyReading()
                            toggleButtons()
                        })
                },
                {},
                resources.getString(R.string.dlg_place_remove_accept_msg),
                resources.getString(R.string.dlg_place_remove_accept_accept),
                resources.getString(R.string.dlg_place_remove_accept_cancel)
            )
        }

        mPanelMarkerControl = rootView.findViewById(R.id.panel_marker_control)

        toggleButtons()
    }

    /**
     * Нажата одна из кнопок из панели с кнопками для добавления маркеров
     */
    private fun _onClickedButttonFromPanel(clickedButton: ImageButton) {
        _setMapEditModeIfNotThis(MapEditMode.Add)
        toggleButtons()
    }

    /**
     * Отображение диалога с действиями над маркером
     */
    fun _showMarkerDialogActions(callbackItemClicked: (DialogInterface, Int, MarkerActions) -> Unit) {
        val actions = arrayOf(
            resources.getString(R.string.dlg_marker_remove),
            resources.getString(R.string.dlg_marker_rename)
        )
        val adapter =
            ArrayAdapter<String>(context!!, android.R.layout.select_dialog_item, actions)
        val builder = AlertDialog.Builder(context)
        builder.setTitle(resources.getString(R.string.dlg_marker_select_action))
        builder.setAdapter(adapter) { dialogInterface, position ->
            when (position) {
                0 -> callbackItemClicked(dialogInterface, position, MarkerActions.Move)
                1 -> callbackItemClicked(dialogInterface, position, MarkerActions.Remove)
                2 -> callbackItemClicked(dialogInterface, position, MarkerActions.Rename)
            }

        }
        val dialog = builder.create()
        dialog.show()
    }
}