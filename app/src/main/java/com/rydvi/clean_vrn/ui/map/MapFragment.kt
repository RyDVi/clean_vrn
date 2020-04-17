package com.rydvi.clean_vrn.ui.map

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.Polygon
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.rydvi.clean_vrn.R
import com.rydvi.clean_vrn.api.DataRepository
import com.rydvi.clean_vrn.api.Place
import com.rydvi.clean_vrn.ui.dialog.Dialog
import com.rydvi.clean_vrn.ui.utils.GameStatus
import com.rydvi.clean_vrn.ui.utils.isAdmin


class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
    GoogleMap.OnMapClickListener, GoogleMap.OnMarkerDragListener {

    private val VORONEZH_LOCATION = LatLng(51.668239, 39.192099)

    private lateinit var mSearchView: SearchView
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

    private lateinit var mBtnRemoveLastPoint: ImageButton

    private lateinit var mapViewModel: MapViewModel

    private var mapEditMode = MapEditMode.Reading
    private var mapPlaceMode = MapPlaceMode.Nothing
    private var markerActionMode = MarkerActions.Nothing
    private var currentMarker: Marker? = null
    private var questZone: Polygon? = null
    private var startPlace: Marker? = null
    private lateinit var lastLocation: LatLng

    private lateinit var markerControl: MarkerControl
    private lateinit var polygonControl: PolygonControl
    private lateinit var placesSearch: PlacesSearch

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
            markerControl = MarkerControl(map, activity!!)
            map.setOnMapClickListener(this)
            map.setOnMarkerClickListener(this)
            map.setOnMarkerDragListener(this)

            polygonControl = PolygonControl(map, context!!)

            placesSearch = PlacesSearch(mSearchView, map, context!!)
            mSearchView.setOnQueryTextListener(placesSearch)

//            mapViewModel.getPlaces()?.observe(this, Observer { places ->
//                for (place in places) {
//                    if (place.placeType === MapPlaceMode.QuestZone.getPlaceId()) {
//                        polygonControl.createFromPoints(place.getGooglePolygonPoints()!!)
//                    } else {
//                        markerControl.addMarkerByPlaceTypeID(
//                            place.point!!.toGoogleLatLng(),
//                            place.id,
//                            place.placeType!!,
//                            place.description!!
//                        )
//                    }
//                }
//            })

            // Move camera to voronezh position
            map.moveCamera(CameraUpdateFactory.newLatLng(VORONEZH_LOCATION))
            map.animateCamera(CameraUpdateFactory.zoomTo(12.0f))
            map.uiSettings.isZoomControlsEnabled = true
            map.isMyLocationEnabled = true
            mMapView.onResume()

            toggleButtons()
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
                        mapViewModel.createPlace(
                            Place().apply {
                                point =
                                    com.rydvi.clean_vrn.api.LatLng()
                                        .parseGoogleLatLng(clickLocation)
                                placeType = MapPlaceMode.Toilet.getPlaceId()
                            },
                            { place -> markerControl.addToilet(clickLocation, place.id, null) {} },
                            { })
                        setModeOnlyReading()
                    }
                    MapPlaceMode.GarbagePlace -> {
                        mapViewModel.createPlace(
                            Place().apply {
                                point =
                                    com.rydvi.clean_vrn.api.LatLng()
                                        .parseGoogleLatLng(clickLocation)
                                placeType = MapPlaceMode.GarbagePlace.getPlaceId()
                            },
                            { place -> markerControl.addGarbage(clickLocation, place.id, null) {} },
                            { })
                        setModeOnlyReading()
                    }
                    MapPlaceMode.AnotherPlace -> {
                        mapViewModel.createPlace(
                            Place().apply {
                                point =
                                    com.rydvi.clean_vrn.api.LatLng()
                                        .parseGoogleLatLng(clickLocation)
                                placeType = MapPlaceMode.AnotherPlace.getPlaceId()
                            },
                            { place ->
                                markerControl.addAnotherPlace(
                                    clickLocation,
                                    place.id,
                                    null
                                ) {}
                            },
                            { })
                        setModeOnlyReading()
                    }
                    MapPlaceMode.StartPlace -> {
                        mapViewModel.createPlace(
                            Place().apply {
                                point =
                                    com.rydvi.clean_vrn.api.LatLng()
                                        .parseGoogleLatLng(clickLocation)
                                placeType = MapPlaceMode.StartPlace.getPlaceId()
                            },
                            { place ->
                                startPlace?.remove()
                                markerControl.addStartPlace(
                                    clickLocation,
                                    place.id,
                                    null
                                ) {
                                    startPlace = it
                                }
                            },
                            { })
                        setModeOnlyReading()
                    }
                    MapPlaceMode.QuestZone -> {
                        polygonControl.addPoint(clickLocation)
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
                mapViewModel.updatePlace(Place().apply {
                    point = com.rydvi.clean_vrn.api.LatLng().parseGoogleLatLng(marker.position)
                    description = marker.snippet
                }, { }, {
                    marker.position = lastLocation
                })
                setModeOnlyReading()
                toggleButtons()
            },
            {
                currentMarker?.position = lastLocation
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
        mBtnRemoveLastPoint.visibility = ImageButton.GONE
        DataRepository.selectedGame?.let {
            if (it.id_status !== GameStatus.completed.getTypeId() && isAdmin()) {
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
                        when (mapPlaceMode) {
                            MapPlaceMode.QuestZone -> {
                                mBtnAccept.show()
                                mBtnRemoveLastPoint.visibility = ImageButton.VISIBLE
                                mPanelAcceptButtons.visibility = LinearLayout.VISIBLE
                                mPanelMarkers.visibility = LinearLayout.GONE
                                mPanelMarkerControl.visibility = LinearLayout.GONE
                            }
                            else -> {
                                mBtnAccept.hide()
                                mPanelAcceptButtons.visibility = LinearLayout.VISIBLE
                                mPanelMarkers.visibility = LinearLayout.GONE
                                mPanelMarkerControl.visibility = LinearLayout.GONE
                            }
                        }
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
            } else {
                mBtnAccept.hide()
                mPanelAcceptButtons.visibility = LinearLayout.VISIBLE
                mPanelMarkers.visibility = LinearLayout.GONE
                mPanelMarkerControl.visibility = LinearLayout.GONE
                mBtnCancel.hide()
            }
            //Сокрытие кнопок дл указания места проведения игры и места старта игры, если игра началась
            if (it.id_status === GameStatus.started.getTypeId()) {
                mBtnSetPlaceQuestZone.visibility = Button.GONE
                mBtnSetPlaceStart.visibility = Button.GONE
            }
        } ?: run {
            mBtnAccept.hide()
            mPanelAcceptButtons.visibility = LinearLayout.VISIBLE
            mPanelMarkers.visibility = LinearLayout.GONE
            mPanelMarkerControl.visibility = LinearLayout.GONE
            mBtnCancel.hide()
        }
    }

    private fun setModeOnlyReading() {
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
            if (startPlace === null) {
                mapPlaceMode = MapPlaceMode.StartPlace
                _onClickedButttonFromPanel(it as ImageButton)
            } else {
                Dialog(activity!!).showDialogAcceptCancel(
                    {
                        mapPlaceMode = MapPlaceMode.StartPlace
                        _onClickedButttonFromPanel(it as ImageButton)
                    },
                    null,
                    resources.getString(R.string.dlg_accept_reset_start_point_msg),
                    resources.getString(R.string.dlg_accept_reset_start_point_ok),
                    resources.getString(R.string.dlg_accept_reset_start_point_cancel)
                )
            }
        }

        mBtnSetPlaceQuestZone = rootView.findViewById(R.id.btn_set_place_quest_zone)
        mBtnSetPlaceQuestZone.setOnClickListener {
            if (questZone === null) {
                mapPlaceMode = MapPlaceMode.QuestZone
                _onClickedButttonFromPanel(it as ImageButton)
            } else {
                Dialog(activity!!).showDialogAcceptCancel(
                    {
                        mapPlaceMode = MapPlaceMode.QuestZone
                        _onClickedButttonFromPanel(it as ImageButton)
                    },
                    null,
                    resources.getString(R.string.dlg_accept_reset_quest_zone_msg),
                    resources.getString(R.string.dlg_accept_reset_quest_zone_ok),
                    resources.getString(R.string.dlg_accept_reset_quest_zone_cancel)
                )
            }
        }

        mPanelMarkers = rootView.findViewById(R.id.panel_markers)

        mBtnAccept = rootView.findViewById(R.id.btn_accept)
        mBtnAccept.setOnClickListener {
            if (mapPlaceMode === MapPlaceMode.QuestZone && mapEditMode === MapEditMode.Add) {
                questZone?.let { polygonControl.removePolygon(it) }
                questZone = polygonControl.endBuild()
                var polygonPoints: Array<com.rydvi.clean_vrn.api.LatLng>? = null
                for (point in questZone!!.points) {
                    polygonPoints = if (polygonPoints === null) {
                        arrayOf(com.rydvi.clean_vrn.api.LatLng().parseGoogleLatLng(point))
                    } else {
                        polygonPoints.plusElement(
                            com.rydvi.clean_vrn.api.LatLng().parseGoogleLatLng(
                                point
                            )
                        )
                    }
                }
                mapViewModel.createPlace(Place().apply {
                    polygon = polygonPoints
                    placeType = MapPlaceMode.QuestZone.getPlaceId()
                }, { }, { })

                setModeOnlyReading()
                toggleButtons()
            } else {
                currentMarker?.let {
                    when (markerActionMode) {
                        MarkerActions.Move -> {
                            mapViewModel.updatePlace(Place().apply {
                                id = it.tag as Long
                                point =
                                    com.rydvi.clean_vrn.api.LatLng().parseGoogleLatLng(it.position)
                            }, {
                                currentMarker?.isDraggable = false
                                toggleButtons()
                            }, {
                                toggleButtons()
                                currentMarker?.remove()
                            })
                        }
                    }
                }
                MarkerActions.Nothing
            }
        }

        mBtnCancel = rootView.findViewById(R.id.btn_cancel)
        mBtnCancel.setOnClickListener {
            if (mapPlaceMode === MapPlaceMode.QuestZone) {
                polygonControl.removePolygon()
            }
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
                    val descriptionText = descriptionInput.text.toString()
                    mapViewModel.updatePlace(Place().apply {
                        description = descriptionText
                        id = (currentMarker!!.tag as MarkerUnique).id
                        point = com.rydvi.clean_vrn.api.LatLng()
                            .parseGoogleLatLng(currentMarker!!.position)
                    }, { currentMarker?.snippet = descriptionText }, { })

                    currentMarker?.hideInfoWindow()
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
                    currentMarker?.let { currentMarker ->
                        mapViewModel.removePlace(
                            (currentMarker.tag as MarkerUnique).id!!,
                            {
                                currentMarker.remove()
                                setModeOnlyReading()
                                toggleButtons()
                            },
                            {
                                setModeOnlyReading()
                                toggleButtons()
                            })
                    }
                },
                {},
                resources.getString(R.string.dlg_place_remove_accept_msg),
                resources.getString(R.string.dlg_place_remove_accept_accept),
                resources.getString(R.string.dlg_place_remove_accept_cancel)
            )
        }

        mPanelMarkerControl = rootView.findViewById(R.id.panel_marker_control)

        mSearchView = rootView.findViewById(R.id.search_location)

        mBtnRemoveLastPoint = rootView.findViewById(R.id.btn_remove_last_point)
        mBtnRemoveLastPoint.setOnClickListener {
            if (polygonControl.removeLastPoint().getCurrentPolygon() === null) {
                setModeOnlyReading()
                toggleButtons()
            }
        }

        toggleButtons()
    }

    /**
     * Нажата одна из кнопок из панели с кнопками для добавления маркеров
     */
    private fun _onClickedButttonFromPanel(clickedButton: ImageButton) {
        _setMapEditModeIfNotThis(MapEditMode.Add)
        toggleButtons()
    }
}