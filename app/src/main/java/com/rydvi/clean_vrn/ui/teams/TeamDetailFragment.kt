package com.rydvi.clean_vrn.ui.teams

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.rydvi.clean_vrn.R
import com.rydvi.clean_vrn.api.DataRepository
import com.rydvi.clean_vrn.api.Team
import com.rydvi.clean_vrn.ui.utils.CreateEditMode
import com.rydvi.clean_vrn.ui.utils.GameStatus
import com.rydvi.clean_vrn.ui.utils.isPlayer
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*


class TeamDetailFragment : Fragment() {

    private lateinit var teamsViewModel: TeamsViewModel
    private var team: Team? = null

    private lateinit var btnTeamEdit: FloatingActionButton
    private lateinit var btnTeamDelete: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_team_detail, container, false)

        teamsViewModel =
            ViewModelProviders.of(this).get(TeamsViewModel::class.java)

        teamsViewModel.getTeams().observe(this, Observer { arrayOfTeams ->
            val teams: Array<Team> = arrayOfTeams
            arguments?.let { it ->
                if (it.containsKey(ARG_ITEM_ID)) {
                    val idTeam: Long = it.getLong(ARG_ITEM_ID)
                    team = teams.find { team -> team.id == idTeam }
                    team?.let {
                        activity?.toolbar?.title =
                            activity!!.resources.getString(R.string.title_team_detail) +
                                    " №${team?.number}  ${team?.name}"
                    }
                }
            }

            team?.let {
                teamsViewModel.getCollectedGarbages(it.id!!)
                    .observe(this, Observer { collectedGarbages ->
                        val tableTeamPoints =
                            rootView.findViewById<TableLayout>(R.id.table_team_points)
                        tableTeamPoints.removeAllViews()

                        //Добавление шапки таблицы
                        tableTeamPoints.addView(getTableHeader(inflater))

                        for (collectedGarbage in collectedGarbages) {
                            tableTeamPoints.addView(
                                tableRow(
                                    inflater,
                                    collectedGarbage.garbage_name!!,
                                    collectedGarbage.coefficient.toString()!!,
                                    if (collectedGarbage.count != null)
                                        collectedGarbage.count.toString()!! else "0",
                                    if (collectedGarbage.sum_points != null)
                                        collectedGarbage.sum_points.toString()!! else "0"
                                )
                            )
                        }
                        //Итоговая строка
                        tableTeamPoints.addView(
                            tableRow(
                                inflater,
                                "Всего",
                                "",
                                "",
                                team!!.sumPoints.toString()
                            )
                        )
                    })

            }
        })
        teamsViewModel.refreshTeams()

        btnTeamEdit = rootView.findViewById(R.id.btn_team_edit)
        btnTeamEdit.setOnClickListener {
            activity!!.findNavController(activity!!.nav_host_fragment.id)
                .navigate(R.id.nav_team_create_edit, Bundle().apply {
                    putLong(TeamCreateEditFragment.TEAM_ID, team!!.id!!)
                    putString(TeamCreateEditFragment.TEAM_MODE, CreateEditMode.EDIT.getMode())
                })
        }

        btnTeamDelete = rootView.findViewById(R.id.btn_team_delete)
        btnTeamDelete.setOnClickListener {
            teamsViewModel.deleteTeam(team!!.id!!) {
                activity!!.runOnUiThread {
                    Toast.makeText(
                        activity,
                        resources.getString(R.string.msg_team_deleted),
                        Toast.LENGTH_LONG
                    ).show()
                }
                _navToTeams()
            }
        }
        toggleButtons()
        return rootView
    }

    private fun tableRow(
        inflater: LayoutInflater,
        garbageName: String,
        coefficient: String,
        count: String,
        sumPoints: String
    ): TableRow {
        val tableRow = inflater.inflate(R.layout.table_row_team_points, null, false)
        val textClass = tableRow.findViewById<TextView>(R.id.text_table_class)
        textClass.text = garbageName
        val textCount = tableRow.findViewById<TextView>(R.id.text_table_count)
        textCount.text = count
        val textCoef = tableRow.findViewById<TextView>(R.id.text_table_coefficient)
        textCoef.text = coefficient
        val textPoints = tableRow.findViewById<TextView>(R.id.text_table_points)
        textPoints.text = sumPoints
        return tableRow as TableRow
    }

    private fun rowText(text: String): TextView {
        val rowText = TextView(activity)
        rowText.text = text
        return rowText
    }

    private fun getTableHeader(inflater: LayoutInflater): TableRow {
        val tableRowHeader =
            inflater.inflate(R.layout.table_row_team_points, null, false)
        val textClass = tableRowHeader.findViewById<TextView>(R.id.text_table_class)
        textClass.text = getString(R.string.lbl_column_header_class)
        textClass.setTypeface(null, Typeface.BOLD)
        val textCount = tableRowHeader.findViewById<TextView>(R.id.text_table_count)
        textCount.text = getString(R.string.lbl_column_header_count)
        textCount.setTypeface(null, Typeface.BOLD)
        val textCoef =
            tableRowHeader.findViewById<TextView>(R.id.text_table_coefficient)
        textCoef.text = getString(R.string.lbl_column_header_coefficient)
        textCoef.setTypeface(null, Typeface.BOLD)
        val textPoints = tableRowHeader.findViewById<TextView>(R.id.text_table_points)
        textPoints.text = getString(R.string.lbl_column_header_points)
        textPoints.setTypeface(null, Typeface.BOLD)
        return tableRowHeader as TableRow
    }

    private fun _navToTeams() {
        activity!!.runOnUiThread {
            activity!!.findNavController(activity!!.nav_host_fragment.id)
                .navigate(R.id.nav_teams)
        }
    }

    fun toggleButtons() {
        DataRepository.selectedGame?.let {
            if (it.id_status !== GameStatus.completed.getTypeId()) {
                if (isPlayer()) {
                    btnTeamEdit.hide()
                    btnTeamDelete.hide()
                } else {
                    btnTeamEdit.show()
                    btnTeamDelete.show()
                }
            } else {
                btnTeamEdit.hide()
                btnTeamDelete.hide()
            }
        }?:run {
            btnTeamEdit.hide()
            btnTeamDelete.hide()
        }
    }

    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val ARG_ITEM_ID = "item_id"
    }
}