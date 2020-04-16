package com.rydvi.clean_vrn.ui.games

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.rydvi.clean_vrn.R
import com.rydvi.clean_vrn.api.Game
import com.rydvi.clean_vrn.ui.login.afterTextChanged
import com.rydvi.clean_vrn.ui.utils.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.*


class GameCreateEditFragment : Fragment() {

    private lateinit var gamesViewModel: GamesViewModel
    private lateinit var recyclerCoefficients: RecyclerView
    private lateinit var game: Game
    private lateinit var editMode: CreateEditMode

    private lateinit var inpGameName: EditText
    private lateinit var inpGameRoute: EditText
    private lateinit var inpGameDate: EditText
    private lateinit var inpGameTime: EditText
    var hasErrorForm = false

    private lateinit var btnSave: FloatingActionButton

    private var mYear: Int = 0
    private var mMonth: Int = 0
    private var mDay: Int = 0
    private var mHour: Int = 0
    private var mMinute: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_game_create_edit, container, false)

        recyclerCoefficients = rootView.findViewById(R.id.recycler_garbages_coefficients)
        inpGameName = rootView.findViewById(R.id.inp_game_name)
        inpGameRoute = rootView.findViewById(R.id.inp_game_route)
        inpGameDate = rootView.findViewById(R.id.inp_game_date)
        inpGameTime = rootView.findViewById(R.id.inp_game_time)

        editMode = getCreateEditModeByString(arguments!!.getString(GAME_MODE)!!)

        gamesViewModel =
            ViewModelProviders.of(this).get(GamesViewModel::class.java)

        if (editMode === CreateEditMode.EDIT) {
            gamesViewModel.getGames()?.observe(this, Observer { games ->
                val idGame: Long = arguments!!.getLong(GAME_ID, 0)
                game = games.find { currentGame -> currentGame.id == idGame }!!

                inpGameName.setText(game.name)
                inpGameRoute.setText(game.route)

                setupDatetimeFields(rootView, game)
                gamesViewModel.getCoefficients(idGame)?.observe(this, Observer { coefficients ->
                    setupRecyclerCoefficients(idGame)
                })
                game?.let {
                    activity?.toolbar?.title =
                        activity!!.resources.getString(R.string.title_activity_game_edit) + " ${game?.name}"
                }
            })
        } else {
            gamesViewModel.getCoefficients(null)?.observe(this, Observer {
                setupRecyclerCoefficients(null)
            })
            setupDatetimeFields(rootView, null)
        }

        btnSave = rootView.findViewById<FloatingActionButton>(R.id.btn_game_save)
        btnSave.setOnClickListener {
            val dateTime =
                "$mYear-${parseWithZero(mMonth)}-${parseWithZero(mDay)} ${parseWithZero(mHour)}:${parseWithZero(
                    mMinute
                )}:00"
            _updateFormState()
            if (!hasErrorForm) {
                if (editMode === CreateEditMode.EDIT) {
                    if (inpGameName.text.toString() !== game.name || inpGameRoute.text.toString() !== game.route) {
                        gamesViewModel.updateGame(
                            game.id!!, inpGameName.text.toString(),
                            inpGameRoute.text.toString(),
                            dateTime
                        ) {
                            gamesViewModel.updateCoefficients(game.id!!) {
                                navToGameDetail(game.id!!)
                            }
                        }
                    }
                } else {
                    gamesViewModel.createGame(
                        inpGameName.text.toString(),
                        inpGameRoute.text.toString(),
                        dateTime
                    ) { createdGame ->
                        gamesViewModel.createCoefficients(createdGame.id!!) {
                            navToGameDetail(createdGame.id!!)
                        }
                    }
                }
            }
        }

        _setupFormStateUpdater()

        return rootView
    }

    private fun setupDatetimeFields(rootView: View, game: Game?) {
        val calendar = Calendar.getInstance()
        game?.let {
            val currentDatetime = it.datetimeInDate()!!
            //Установка полученного времени игры
            inpGameDate.setText(formatDate(currentDatetime))
            inpGameTime.setText(formatTime(currentDatetime))
            calendar.time = currentDatetime
            mYear = calendar.get(Calendar.YEAR)
            mMonth = calendar.get(Calendar.MONTH) + 1
            mDay = calendar.get(Calendar.DAY_OF_MONTH)
            mHour = calendar.get(Calendar.HOUR_OF_DAY)
            mMinute = calendar.get(Calendar.MINUTE)
        }

        val btnGameDate = rootView.findViewById<ImageButton>(R.id.btn_game_date)
        btnGameDate.setOnClickListener {
            if (game == null) {
                mYear = calendar.get(Calendar.YEAR)
                mMonth = calendar.get(Calendar.MONTH)
                mDay = calendar.get(Calendar.DAY_OF_MONTH)
            }
            val datePickerDialog = DatePickerDialog(
                activity!!,
                OnDateSetListener { _, year, month, day ->
                    mDay = day
                    mMonth = month + 1
                    mYear = year
                    val resultDay = if (day < 10) "0$day" else day
                    val resultMonth = if (month < 10) "0${month + 1}" else month + 1
                    inpGameDate.setText("$resultDay.$resultMonth.$year")
                    inpGameDate.apply {
                        afterTextChanged {
                            _updateFormState()
                        }
                    }
                },
                mYear,
                mMonth,
                mDay
            )
            datePickerDialog.show()
        }

        val btnGameTime = rootView.findViewById<ImageButton>(R.id.btn_game_time)
        btnGameTime.setOnClickListener {
            if(game==null){
                mHour = calendar.get(Calendar.HOUR_OF_DAY)
                mMinute = calendar.get(Calendar.MINUTE)
            }
            val timePickerDialog = TimePickerDialog(
                activity!!,
                TimePickerDialog.OnTimeSetListener { view, hour, minute ->
                    mHour = hour
                    mMinute = minute
                    val resultHour = if (hour < 10) "0$hour" else hour
                    val resultMinute = if (minute < 10) "0$minute" else minute
                    inpGameTime.setText("$resultHour:$resultMinute")
                    inpGameTime.apply {
                        afterTextChanged {
                            _updateFormState()
                        }
                    }
                },
                mHour,
                mMinute,
                true
            )
            timePickerDialog.show()
        }
    }

    private fun _setupFormStateUpdater() {
        inpGameRoute.apply {
            afterTextChanged {
                _updateFormState()
            }
        }
        inpGameDate.apply {
            afterTextChanged {
                _updateFormState()
            }
        }
        inpGameTime.apply {
            afterTextChanged {
                _updateFormState()
            }
        }
        inpGameName.apply {
            afterTextChanged {
                _updateFormState()
            }
        }
        _updateFormState()
    }

    private fun _updateFormState() {
        hasErrorForm = false
        val resources = activity!!.resources
        if (inpGameDate.text.toString() == "") {
            hasErrorForm = true
            inpGameDate.error = resources.getString(R.string.err_inp_game_date)
        } else {
            inpGameDate.error = null
        }
        if (inpGameTime.text.toString() == "") {
            hasErrorForm = true
            inpGameTime.error = resources.getString(R.string.err_inp_game_time)
        } else {
            inpGameTime.error = null
        }
        if (inpGameName.text.toString() == "") {
            hasErrorForm = true
            inpGameName.error = resources.getString(R.string.err_inp_game_name)
        } else {
            inpGameName.error = null
        }
        btnSave.isEnabled = !hasErrorForm
        //Не запрещаем нажимать кнопку, но статус всё-равно меняем на ошибку
        recyclerCoefficients?.adapter?.let {
            if ((it as GarbageCoefficientItemRecyclerViewAdapter).onCheckTextsHasError()) {
                hasErrorForm = true
            }
        }
    }

    fun setupRecyclerCoefficients(idGame: Long?) {
        val adapterGarbageCoefficients =
            GarbageCoefficientItemRecyclerViewAdapter(
                activity!!,
                gamesViewModel.getCoefficients(idGame),
                CreateEditMode.EDIT
            )
        val linearLayoutManagerVertical = LinearLayoutManager(activity!!)
        linearLayoutManagerVertical.orientation = LinearLayoutManager.VERTICAL

        recyclerCoefficients.apply {
            layoutManager = linearLayoutManagerVertical
            adapter = adapterGarbageCoefficients
        }
    }

    fun navToGameDetail(idGame: Long) {
        activity!!.runOnUiThread {
            activity!!.findNavController(activity!!.nav_host_fragment.id)
                .navigate(
                    R.id.nav_game_detail, Bundle().apply {
                        putLong(GameDetailFragment.ARG_ITEM_ID, idGame)
                    }, NavOptions.Builder()
                        .setPopUpTo(
                            R.id.nav_games,
                            true
                        ).build()
                )
        }
    }

    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val GAME_ID = "game_id"
        const val GAME_MODE = "game_create_edit_mode"
    }
}

