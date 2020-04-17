package com.rydvi.clean_vrn.ui.teams

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.rydvi.clean_vrn.MainActivity
import com.rydvi.clean_vrn.R
import com.rydvi.clean_vrn.api.DataRepository
import com.rydvi.clean_vrn.api.Team
import com.rydvi.clean_vrn.ui.utils.CreateEditMode
import com.rydvi.clean_vrn.ui.utils.GameStatus
import com.rydvi.clean_vrn.ui.utils.isAdmin
import com.rydvi.clean_vrn.ui.utils.isPlayer
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.team_list.*

class TeamsFragment : Fragment() {

    private var twoPane: Boolean = false
    private lateinit var teamsViewModel: TeamsViewModel
    private lateinit var swipeRefreshTeams: SwipeRefreshLayout

    private lateinit var btnTeamAdd:FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_teams, container, false)
        teamsViewModel =
            ViewModelProviders.of(this).get(TeamsViewModel::class.java)
        teamsViewModel.getTeams().observe(this, Observer {
            val teamList: RecyclerView = root.findViewById(R.id.team_list)
            setupRecyclerView(teamList, it)
        })
        if (team_detail_container != null) {
            twoPane = true
        }

        btnTeamAdd = root.findViewById(R.id.btn_team_add)
        btnTeamAdd.setOnClickListener {
            activity!!.findNavController(activity!!.nav_host_fragment.id)
                .navigate(R.id.nav_team_create_edit, Bundle().apply {
                    putString(TeamCreateEditFragment.TEAM_MODE, CreateEditMode.CREATE.getMode())
                })
        }

        teamsViewModel.refreshTeams()

        swipeRefreshTeams = root.findViewById(R.id.swipeRefreshTeams)
        swipeRefreshTeams.setOnRefreshListener { onRefreshTeamsRecyclerView() }
        toggleButtons()
        return root
    }

    private fun setupRecyclerView(
        recyclerView: RecyclerView,
        teams: Array<Team>
    ) {
        teams.sortWith(compareBy({ it.name?.toUpperCase() }, { it.sumPoints }))
        recyclerView.adapter =
            TeamItemRecyclerViewAdapter(activity!!, teams.toList(), twoPane)
    }

    private fun onRefreshTeamsRecyclerView() {
        teamsViewModel = ViewModelProviders.of(this).get(TeamsViewModel::class.java)
        teamsViewModel.refreshTeams()?.observe(this, Observer {
            swipeRefreshTeams.isRefreshing = false
        })
    }

    fun toggleButtons(){
        DataRepository.selectedGame?.let {
            if(it.id_status!==GameStatus.completed.getTypeId()){
                if (isPlayer()) btnTeamAdd.hide() else btnTeamAdd.show()
            } else {
                btnTeamAdd.hide()
            }
        }?:run {
            btnTeamAdd.hide()
        }
    }
}