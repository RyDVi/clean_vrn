package com.rydvi.clean_vrn.ui.teams

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.rydvi.clean_vrn.R
import com.rydvi.clean_vrn.api.Team
import com.rydvi.clean_vrn.ui.games.GamesViewModel
import kotlinx.android.synthetic.main.team_list.*

class TeamsFragment : Fragment() {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private var twoPane: Boolean = false
    private lateinit var teamsViewModel: TeamsViewModel
    private lateinit var swipeRefreshTeams: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        teamsViewModel =
            ViewModelProviders.of(this).get(TeamsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        teamsViewModel.getTeams().observe(this, Observer {
            val teamList:RecyclerView = root.findViewById(R.id.team_list)
            setupRecyclerView(teamList, it)
        })
        if (team_detail_container != null) {
            twoPane = true
        }

        swipeRefreshTeams = root.findViewById(R.id.swipeRefreshTeams)
        swipeRefreshTeams.setOnRefreshListener { onRefreshTeamsRecyclerView() }
        return root
    }

    private fun setupRecyclerView(
        recyclerView: RecyclerView,
        teams: Array<Team>
    ) {
        recyclerView.adapter =
            TeamItemRecyclerViewAdapter(activity!!, teams.toList(), twoPane)
    }

    private fun onRefreshTeamsRecyclerView(){
        teamsViewModel = ViewModelProviders.of(this).get(TeamsViewModel::class.java)
        teamsViewModel.refreshTeams()?.observe(this, Observer {
            swipeRefreshTeams.isRefreshing = false
        })
    }
}