package com.rydvi.clean_vrn.ui.games

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rydvi.clean_vrn.R
import com.rydvi.clean_vrn.api.Game
import com.rydvi.clean_vrn.ui.utils.CreateEditMode

import kotlinx.android.synthetic.main.activity_game_create_edit.*

class GameCreateEditActivity : AppCompatActivity() {

    private lateinit var gamesViewModel: GamesViewModel
    private lateinit var recyclerCoefficients: RecyclerView
    private lateinit var game: Game
    private lateinit var editMode: CreateEditMode

    private lateinit var inpGameName: EditText
    private lateinit var inpGameRoute: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_create_edit)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerCoefficients = findViewById(R.id.recycler_garbages_coefficients)
        inpGameName = findViewById(R.id.inp_game_name)
        inpGameRoute = findViewById(R.id.inp_game_route)

        editMode = intent.extras!![GAME_MODE] as CreateEditMode


        gamesViewModel =
            ViewModelProviders.of(this).get(GamesViewModel::class.java)

        if (editMode === CreateEditMode.EDIT) {
            gamesViewModel.getGames()?.observe(this, Observer { games ->
                val idGame: Long = intent.getLongExtra(GAME_ID, 0)
                game = games.find { currentGame -> currentGame.id == idGame }!!

                inpGameName.setText(game.name)
                inpGameRoute.setText(game.route)


                gamesViewModel.getCoefficients(idGame)?.observe(this, Observer { coefficients ->
                    setupRecyclerCoefficients(idGame)
                })
            })
        } else {
            //TODO: Создание игры и коэффициентов (сначала игра, затем из игры её ИД и потом коэффициенты)
            gamesViewModel.getCoefficients(null)?.observe(this, Observer {
                setupRecyclerCoefficients(null)
            })
        }


        val btnSave = findViewById<Button>(R.id.btn_game_save)
        btnSave.setOnClickListener {
            val intent = Intent(this, GameDetailActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            android.R.id.home -> {
                NavUtils.navigateUpTo(this, Intent(this, GamesListActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }


    fun setupRecyclerCoefficients(idGame:Long?){
        val adapterGarbagesCoefficients =
            GarbageCoefficientItemRecyclerViewAdapter(
                this,
                gamesViewModel.getCoefficients(idGame)
            )
        val linearLayoutManagerVertical = LinearLayoutManager(this)
        linearLayoutManagerVertical.orientation = LinearLayoutManager.VERTICAL

        recyclerCoefficients.apply {
            layoutManager = linearLayoutManagerVertical
            adapter = adapterGarbagesCoefficients
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
