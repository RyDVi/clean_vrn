package com.rydvi.clean_vrn.ui.teams

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.rydvi.clean_vrn.R
import com.rydvi.clean_vrn.api.Team
import com.rydvi.clean_vrn.ui.utils.CreateEditMode
import kotlinx.android.synthetic.main.activity_team_create_edit.*

class TeamCreateEditActivity : AppCompatActivity() {

    private lateinit var teamsViewModel: TeamsViewModel
    private lateinit var team: Team
    private lateinit var recyclerViewCollectedGarbarages: RecyclerView
    private lateinit var mode: CreateEditMode
    private lateinit var inpTeamName: TextInputEditText
    private lateinit var inpTeamNumber: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_team_create_edit)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mode = intent.extras!![TEAM_MODE] as CreateEditMode

        recyclerViewCollectedGarbarages =
            findViewById(R.id.recycler_team_collected_garbages)

        inpTeamName = findViewById(R.id.inp_team_name)
        inpTeamNumber = findViewById(R.id.inp_team_number)

        teamsViewModel =
            ViewModelProviders.of(this).get(TeamsViewModel::class.java)
        if (mode === CreateEditMode.EDIT) {
            teamsViewModel.getTeams().observe(this, Observer { teams ->
                val idTeam: Long = intent.getLongExtra(TEAM_ID, 0)
                team = teams.find { currentTeam -> currentTeam.id == idTeam }!!

                inpTeamName.setText(team.name)
                inpTeamNumber.setText(team.number.toString())


                teamsViewModel.getCollectedGarbages(idTeam).observe(this, Observer { garbages ->
                    val adapterCollectedGarbages =
                        CollectedGarbarageItemRecyclerViewAdapter(
                            this,
                            teamsViewModel.getCollectedGarbages(idTeam)
                        )
                    val linearLayoutManagerVertical = LinearLayoutManager(this)
                    linearLayoutManagerVertical.orientation = LinearLayoutManager.VERTICAL


                    recyclerViewCollectedGarbarages.apply {
                        layoutManager = linearLayoutManagerVertical
                        adapter = adapterCollectedGarbages
                    }
                })
            })
        } else {
            findViewById<LinearLayout>(R.id.vertical_layout_collected_garbages).visibility =
                View.INVISIBLE
        }

        val btnSave = findViewById<Button>(R.id.btn_team_save)
        btnSave.setOnClickListener {
            if (mode.getMode() === CreateEditMode.EDIT.getMode()) {
                if (inpTeamName.text.toString() !== team.name ||
                    inpTeamNumber.text.toString() !== team.number.toString()
                ) {
                    teamsViewModel.updateTeam(
                        team.id!!,
                        inpTeamName.text.toString(),
                        inpTeamNumber.text.toString().toLong()
                    ) {
                        _updateCollectedGarbagesAndNavToTeam(team.id!!)
                    }
                } else {
                    _updateCollectedGarbagesAndNavToTeam(team.id!!)
                }

            } else {
                //TODO: Создание новой команды
                teamsViewModel.createTeam(
                    inpTeamName.text.toString(),
                    inpTeamNumber.text.toString().toLong()
                ) {
                    _navToTeam(it.id!!)
                }
            }
        }

    }

    private fun _updateCollectedGarbagesAndNavToTeam(idTeam: Long) {
        teamsViewModel.updateCollectedGarbages(idTeam, {
            _navToTeam(idTeam)
        }, {
            //TODO: Failed post collected garbages request
        })
    }

    private fun _navToTeam(idTeam: Long) {
        val intent = Intent(this, TeamDetailActivity::class.java).apply {
            putExtra(
                TeamDetailFragment.ARG_ITEM_ID,
                idTeam
            )
        }
        startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            android.R.id.home -> {
                NavUtils.navigateUpTo(this, Intent(this, TeamListActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val TEAM_ID = "team_id"
        const val TEAM_MODE = "team_create_edit_mode"
    }

}
