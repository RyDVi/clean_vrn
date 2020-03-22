package com.rydvi.clean_vrn.ui.teams

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rydvi.clean_vrn.R
import com.rydvi.clean_vrn.api.CollectedGarbage
import com.rydvi.clean_vrn.api.Team
import com.rydvi.clean_vrn.ui.organizators.OrganizatorDetailActivity
import com.rydvi.clean_vrn.ui.organizators.OrganizatorListActivity

import kotlinx.android.synthetic.main.activity_team_create_edit.*

class TeamCreateEditActivity : AppCompatActivity() {

    private lateinit var teamsViewModel: TeamsViewModel
    private lateinit var team: Team
    private lateinit var recyclerViewCollectedGarbarages: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_team_create_edit)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val btnSave = findViewById<Button>(R.id.btn_team_save)
        btnSave.setOnClickListener {
            val intent = Intent(this, TeamDetailActivity::class.java)
            startActivity(intent)
        }

//        if (savedInstanceState == null) {
//            val fragment = TeamDetailFragment().apply {
//                arguments = Bundle().apply {
//                    putLong(
//                        TeamDetailFragment.ARG_ITEM_ID,
//                        intent.getLongExtra(TeamDetailFragment.ARG_ITEM_ID, 0)
//                    )
//                }
//            }
//        }
        teamsViewModel =
            ViewModelProviders.of(this).get(TeamsViewModel::class.java)
        teamsViewModel.getTeams().observe(this, Observer {
            val idTeam: Long = intent.getLongExtra(TEAM_ID, 0)
            team = it.find { currentTeam -> currentTeam.id == idTeam }!!

            teamsViewModel.getCollectedGarbages(idTeam).observe(this, Observer { garbages ->
                val adapterCollectedGarbages =
                    CollectedGarbarageItemRecyclerViewAdapter(this, garbages.toList())
                val linearLayoutManagerVertical = LinearLayoutManager(this)
                linearLayoutManagerVertical.orientation = LinearLayoutManager.VERTICAL
                recyclerViewCollectedGarbarages =
                    findViewById<RecyclerView>(R.id.recycler_team_collected_garbages).apply {
                        layoutManager = linearLayoutManagerVertical
                        adapter = adapterCollectedGarbages
                    }
            })
        })


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
    }

}
