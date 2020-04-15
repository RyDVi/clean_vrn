package com.rydvi.clean_vrn.ui.games

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
import com.rydvi.clean_vrn.MainActivity
import com.rydvi.clean_vrn.R
import com.rydvi.clean_vrn.api.Game
import com.rydvi.clean_vrn.ui.login.LoginViewModelFactory
import com.rydvi.clean_vrn.ui.login.afterTextChanged
import com.rydvi.clean_vrn.ui.utils.CreateEditMode
import com.rydvi.clean_vrn.ui.utils.getCreateEditModeByString
import com.rydvi.clean_vrn.ui.utils.parseWithZero
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
        }


        btnSave = rootView.findViewById<FloatingActionButton>(R.id.btn_game_save)
        btnSave.setOnClickListener {
            val dateTime =
                "$mYear-${parseWithZero(mMonth)}-${parseWithZero(mDay)}T${parseWithZero(mHour)}:${parseWithZero(
                    mMinute
                )}:00Z"
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

        val btnGameDate = rootView.findViewById<ImageButton>(R.id.btn_game_date)
        btnGameDate.setOnClickListener {
            val c = Calendar.getInstance()
            mYear = c.get(Calendar.YEAR)
            mMonth = c.get(Calendar.MONTH)
            mDay = c.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(
                activity!!,
                OnDateSetListener { view, year, month, day ->
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
            val c = Calendar.getInstance()
            mHour = c.get(Calendar.HOUR_OF_DAY)
            mMinute = c.get(Calendar.MINUTE)
            val timePickerDialog = TimePickerDialog(
                activity!!,
                TimePickerDialog.OnTimeSetListener { view, hour, minute ->
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

        _setupFormStateUpdater()

        return rootView
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
    }

    fun setupRecyclerCoefficients(idGame: Long?) {
        val adapterGarbagesCoefficients =
            GarbageCoefficientItemRecyclerViewAdapter(
                activity!!,
                gamesViewModel.getCoefficients(idGame),
                CreateEditMode.EDIT
            )
        val linearLayoutManagerVertical = LinearLayoutManager(activity!!)
        linearLayoutManagerVertical.orientation = LinearLayoutManager.VERTICAL

        recyclerCoefficients.apply {
            layoutManager = linearLayoutManagerVertical
            adapter = adapterGarbagesCoefficients
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

