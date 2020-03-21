package com.rydvi.clean_vrn.ui.teams

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
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

        teamsViewModel.getTeams().observe(this, Observer { arrayOfTeams ->
            val teams: Array<Team> = arrayOfTeams
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
//            team?.let {
//                rootView.team_sum_points.text = it.sumPoints.toString()
//            }

            team?.let {
                teamsViewModel.getCollectedGarbages(it.id!!)
                    .observe(this, Observer { collectedGarbages ->
                        val tableTeamPoints =
                            activity!!.findViewById<TableLayout>(R.id.table_team_points)
                        tableTeamPoints.removeAllViews()

                        //Добавление шапки таблицы
                        tableTeamPoints.addView(getTableHeader(inflater))

                        for (collectedGarbage in collectedGarbages) {
                            tableTeamPoints.addView(
                                tableRow(
                                    inflater,
                                    collectedGarbage.garbage_name!!,
                                    collectedGarbage.coefficient.toString()!!,
                                    collectedGarbage.count.toString()!!,
                                    collectedGarbage.sum_points.toString()!!
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
        textCount.text = coefficient.toString()
        val textCoef = tableRow.findViewById<TextView>(R.id.text_table_coefficient)
        textCoef.text = count.toString()
        val textPoints = tableRow.findViewById<TextView>(R.id.text_table_points)
        textPoints.text = sumPoints.toString()
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

    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val ARG_ITEM_ID = "item_id"
    }
}
