package com.rydvi.clean_vrn.ui.teams

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.rydvi.clean_vrn.R
import com.rydvi.clean_vrn.api.CollectedGarbage
import kotlinx.android.synthetic.main.collected_garbarage_edit_listitem.view.*

class CollectedGarbarageItemRecyclerViewAdapter(
    private val parentActivity: FragmentActivity,
    private val values: List<CollectedGarbage>
) :
    RecyclerView.Adapter<CollectedGarbarageItemRecyclerViewAdapter.ViewHolder>() {


    init {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.collected_garbarage_edit_listitem, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.countView.text = item.count.toString()
        holder.classView.text = item.garbage_name.toString()

        with(holder.itemView) {
            tag = item
        }
    }

    override fun getItemCount() = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val countView:TextView = view.text_count
        val classView:TextView = view.text_garbage_name
//        val nameView: TextView = view.team_name
//        val numberView: TextView = view.team_number
//        val sumPointsView: TextView = view.team_sum_points
    }
}