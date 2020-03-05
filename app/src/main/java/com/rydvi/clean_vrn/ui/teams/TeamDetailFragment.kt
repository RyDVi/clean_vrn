package com.rydvi.clean_vrn.ui.teams

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.rydvi.clean_vrn.R
import com.rydvi.clean_vrn.api.Team
import kotlinx.android.synthetic.main.activity_team_detail.*
import kotlinx.android.synthetic.main.team_detail.view.*

/**
 * A fragment representing a single Team detail screen.
 * This fragment is either contained in a [TeamListActivity]
 * in two-pane mode (on tablets) or a [TeamDetailActivity]
 * on handsets.
 */
class TeamDetailFragment : Fragment() {

    private lateinit var teamsViewModel: TeamsViewModel
    private var team: Team? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.team_detail, container, false)
        teamsViewModel =
            ViewModelProviders.of(this).get(TeamsViewModel::class.java)

        teamsViewModel.getTeams().observe(this, Observer {
            val teams: Array<Team> = it
            arguments?.let { it ->
                if (it.containsKey(ARG_ITEM_ID)) {
                    val idTeam: Long = it.getLong(ARG_ITEM_ID)
                    team = teams.find { team -> team.id == idTeam }
                    team?.let {
                        activity?.toolbar_layout?.title =
                            "№${team?.number} " + activity!!.resources.getString(R.string.title_team_detail) + " ${team?.name}"
                    }
                }
            }
            team?.let {
                rootView.team_sum_points.text = it.sumPoints.toString()
            }
        })
        teamsViewModel.refreshGames()

        return rootView
    }

    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val ARG_ITEM_ID = "item_id"
    }
}
