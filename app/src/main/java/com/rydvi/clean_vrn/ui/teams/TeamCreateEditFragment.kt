package com.rydvi.clean_vrn.ui.teams


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.rydvi.clean_vrn.MainActivity
import com.rydvi.clean_vrn.R
import com.rydvi.clean_vrn.api.Team
import com.rydvi.clean_vrn.ui.games.GameDetailFragment
import com.rydvi.clean_vrn.ui.utils.CreateEditMode
import com.rydvi.clean_vrn.ui.utils.getCreateEditModeByString
import kotlinx.android.synthetic.main.content_main.*

/**
 * A simple [Fragment] subclass.
 */
class TeamCreateEditFragment : Fragment() {

    private lateinit var teamsViewModel: TeamsViewModel
    private lateinit var team: Team
    private lateinit var recyclerViewCollectedGarbarages: RecyclerView
    private lateinit var editMode: CreateEditMode
    private lateinit var inpTeamName: TextInputEditText
    private lateinit var inpTeamNumber: TextInputEditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_team_create_edit, container, false)
        (activity as MainActivity).showLoading(true)

        editMode = getCreateEditModeByString(arguments!!.getString(TEAM_MODE)!!)

        recyclerViewCollectedGarbarages =
            rootView.findViewById(R.id.recycler_team_collected_garbages)

        inpTeamName = rootView.findViewById(R.id.inp_team_name)
        inpTeamNumber = rootView.findViewById(R.id.inp_team_number)

        teamsViewModel =
            ViewModelProviders.of(this).get(TeamsViewModel::class.java)
        if (editMode === CreateEditMode.EDIT) {
            teamsViewModel.getTeams().observe(this, Observer { teams ->
                val idTeam: Long = arguments!!.getLong(TEAM_ID, 0)
                team = teams.find { currentTeam -> currentTeam.id == idTeam }!!

                inpTeamName.setText(team.name)
                inpTeamNumber.setText(team.number.toString())


                teamsViewModel.getCollectedGarbages(idTeam).observe(this, Observer { garbages ->
                    val adapterCollectedGarbages =
                        CollectedGarbarageItemRecyclerViewAdapter(
                            activity!!,
                            teamsViewModel.getCollectedGarbages(idTeam)
                        )
                    val linearLayoutManagerVertical = LinearLayoutManager(activity!!)
                    linearLayoutManagerVertical.orientation = LinearLayoutManager.VERTICAL


                    recyclerViewCollectedGarbarages.apply {
                        layoutManager = linearLayoutManagerVertical
                        adapter = adapterCollectedGarbages
                    }
                    (activity as MainActivity).showLoading(false)
                })
            })
        } else {
            rootView.findViewById<LinearLayout>(R.id.vertical_layout_collected_garbages).visibility =
                View.INVISIBLE
            (activity as MainActivity).showLoading(false)
        }

        val btnTeamSave = rootView.findViewById<FloatingActionButton>(R.id.btn_team_save)
        btnTeamSave.setOnClickListener {
            (activity as MainActivity).showLoading(true)
            if (editMode.getMode() === CreateEditMode.EDIT.getMode()) {
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
                teamsViewModel.createTeam(
                    inpTeamName.text.toString(),
                    inpTeamNumber.text.toString().toLong()
                ) {
                    _navToTeam(it.id!!)
                }
            }
        }

        return rootView
    }

    private fun _updateCollectedGarbagesAndNavToTeam(idTeam: Long) {
        teamsViewModel.updateCollectedGarbages(idTeam, {
            _navToTeam(idTeam)
        }, {
            //TODO: Failed post collected garbages request
        })
    }

    private fun _navToTeam(idTeam: Long) {
        activity!!.runOnUiThread {
            activity!!.findNavController(activity!!.nav_host_fragment.id)
                .navigate(
                    R.id.nav_team_detail, Bundle().apply {
                        putLong(TeamDetailFragment.ARG_ITEM_ID, idTeam)
                    }, NavOptions.Builder()
                        .setPopUpTo(
                            R.id.nav_teams,
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
        const val TEAM_ID = "team_id"
        const val TEAM_MODE = "team_create_edit_mode"
    }
}