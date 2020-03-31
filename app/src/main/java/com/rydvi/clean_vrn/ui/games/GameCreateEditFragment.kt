package com.rydvi.clean_vrn.ui.games

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
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
import com.rydvi.clean_vrn.ui.utils.CreateEditMode
import com.rydvi.clean_vrn.ui.utils.getCreateEditModeByString
import kotlinx.android.synthetic.main.content_main.*


class GameCreateEditFragment : Fragment() {

    private lateinit var gamesViewModel: GamesViewModel
    private lateinit var recyclerCoefficients: RecyclerView
    private lateinit var game: Game
    private lateinit var editMode: CreateEditMode

    private lateinit var inpGameName: EditText
    private lateinit var inpGameRoute: EditText
    private lateinit var inpGameDatetime: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_game_create_edit, container, false)

        (activity as MainActivity).showLoading(true)

        recyclerCoefficients = rootView.findViewById(R.id.recycler_garbages_coefficients)
        inpGameName = rootView.findViewById(R.id.inp_game_name)
        inpGameRoute = rootView.findViewById(R.id.inp_game_route)
        inpGameDatetime = rootView.findViewById(R.id.inp_game_datetime)

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
                    (activity as MainActivity).showLoading(false)
                })
            })
        } else {
            gamesViewModel.getCoefficients(null)?.observe(this, Observer {
                setupRecyclerCoefficients(null)
                (activity as MainActivity).showLoading(false)
            })
        }


        val btnSave = rootView.findViewById<FloatingActionButton>(R.id.btn_game_save)
        btnSave.setOnClickListener {
            (activity as MainActivity).showLoading(true)
            if (editMode === CreateEditMode.EDIT) {
                if (inpGameName.text.toString() !== game.name || inpGameRoute.text.toString() !== game.route) {
                    gamesViewModel.updateGame(
                        game.id!!, inpGameName.text.toString(),
                        inpGameRoute.text.toString(),
                        inpGameDatetime.text.toString()
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
                    inpGameDatetime.text.toString()
                ) { createdGame ->
                    gamesViewModel.createCoefficients(createdGame.id!!) {
                        navToGameDetail(createdGame.id!!)
                    }
                }
            }

        }

        return rootView
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

