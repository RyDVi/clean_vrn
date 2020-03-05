package com.rydvi.clean_vrn.ui.teams

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.rydvi.clean_vrn.R
import com.rydvi.clean_vrn.api.Team
import kotlinx.android.synthetic.main.team_list_content.view.*

class TeamItemRecyclerViewAdapter(
    private val parentActivity: FragmentActivity,
    private val values: List<Team>,
    private val twoPane: Boolean
) :
    RecyclerView.Adapter<TeamItemRecyclerViewAdapter.ViewHolder>() {

    private val onClickListener: View.OnClickListener

    init {
        onClickListener = View.OnClickListener { v ->
            val item = v.tag as Team
            if (twoPane) {
                val fragment = TeamDetailFragment().apply {
                    arguments = Bundle().apply {
                        putLong(TeamDetailFragment.ARG_ITEM_ID, item.id!!)
                    }
                }
                parentActivity.supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.team_detail_container, fragment)
                    .commit()
            } else {
                val intent = Intent(v.context, TeamDetailActivity::class.java).apply {
                    putExtra(TeamDetailFragment.ARG_ITEM_ID, item.id)
                }
                v.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.team_list_content, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.nameView.text = item.name
        holder.numberView.text = item.number.toString()
        item.sumPoints?.let {
            holder.sumPointsView.text = it.toString()
        }

        with(holder.itemView) {
            tag = item
            setOnClickListener(onClickListener)
        }
    }

    override fun getItemCount() = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameView: TextView = view.team_name
        val numberView: TextView = view.team_number
        val sumPointsView: TextView = view.team_sum_points
    }
}