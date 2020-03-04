package com.rydvi.clean_vrn.ui.games

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.rydvi.clean_vrn.R
import com.rydvi.clean_vrn.api.Game
import kotlinx.android.synthetic.main.game_list_content.view.*

class GameItemRecyclerViewAdapter(
    private val activity: FragmentActivity,
    private val values: List<Game>,
    private val twoPane: Boolean
) :
    RecyclerView.Adapter<GameItemRecyclerViewAdapter.ViewHolder>() {

    private val onClickListener: View.OnClickListener

    init {
        onClickListener = View.OnClickListener { v ->
            val item = v.tag as Game
            if (twoPane) {
                val fragment = GameDetailFragment().apply {
                    arguments = Bundle().apply {
                        item.id?.let {
                            putLong(GameDetailFragment.ARG_ITEM_ID, it)
                        }
                    }
                }
                activity.supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.game_detail_container, fragment)
                    .commit()
            } else {
                val intent = Intent(v.context, GameDetailActivity::class.java).apply {
                    putExtra(GameDetailFragment.ARG_ITEM_ID, item.id)
                }
                v.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.game_list_content, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.nameView.text = item.name.toString()
        when (item.id_status!!.toInt()){
            1 -> holder.nameView.setTextColor(Color.GREEN)
            2 -> holder.nameView.setTextColor(Color.RED)
        }
        holder.descriptionView.text = item.description.toString()
        holder.datetimeView.text = item.datetime.toString()


        with(holder.itemView) {
            tag = item
            setOnClickListener(onClickListener)
        }
    }

    override fun getItemCount() = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameView: TextView = view.name
        val descriptionView: TextView = view.description
        val datetimeView: TextView = view.datetime
    }
}