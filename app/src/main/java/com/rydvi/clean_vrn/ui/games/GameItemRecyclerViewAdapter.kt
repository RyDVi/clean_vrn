package com.rydvi.clean_vrn.ui.games

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.rydvi.clean_vrn.R
import com.rydvi.clean_vrn.api.DataRepository
import com.rydvi.clean_vrn.api.Game
import com.rydvi.clean_vrn.ui.utils.formatDateTime
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.game_list_content.view.*


class GameItemRecyclerViewAdapter(
    private val activity: FragmentActivity,
    private val values: List<Game>,
    private val twoPane: Boolean
) :
    RecyclerView.Adapter<GameItemRecyclerViewAdapter.ViewHolder>() {

    private val onLongClickListener = View.OnLongClickListener { v ->
        val item = v.tag as Game
        activity.findNavController(activity.nav_host_fragment.id)
            .navigate(R.id.nav_game_detail, Bundle().apply {
                putLong(GameDetailFragment.ARG_ITEM_ID, item.id!!)
            })
        true
    }

    private val onClickListener: View.OnClickListener = View.OnClickListener { v ->
        //Устанавливаем выбранную игру
        val item = v.tag as Game
        DataRepository.selectedGame = item
        val gamesViewModel =
            ViewModelProviders.of(activity).get(GamesViewModel::class.java)
        item.id?.let {
            gamesViewModel.selectGame(it, {
                //Запуск главного потока, поскольку findNavController работает только в нем
                Handler(activity.mainLooper).post {
                    activity.findNavController(R.id.nav_host_fragment).navigate(R.id.nav_map)
                }
            }, {})
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
        when (item.id_status!!.toInt()) {
            1 -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.nameView.setTextColor(
                    activity.resources.getColor(
                        R.color.colorTextGameStatusStarted,
                        null
                    )
                )
            } else {
                holder.nameView.setTextColor(activity.resources.getColor(R.color.colorTextGameStatusStarted))
            }
            2 -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.nameView.setTextColor(
                    activity.resources.getColor(
                        R.color.colorTextGameStatusEnded,
                        null
                    )
                )
            } else {
                holder.nameView.setTextColor(activity.resources.getColor(R.color.colorTextGameStatusEnded))
            }
            3 -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.nameView.setTextColor(
                    activity.resources.getColor(
                        R.color.colorTextGameStatusPlanned,
                        null
                    )
                )
            } else {
                holder.nameView.setTextColor(activity.resources.getColor(R.color.colorTextGameStatusPlanned))
            }
        }
        holder.routeView.text = item?.route.let { it } ?: ""

        val date = item.datetimeInDate()
        holder.datetimeView.text = date?.let { formatDateTime(it) } ?: ""

        with(holder.itemView) {
            tag = item
            setOnLongClickListener(onLongClickListener)
            setOnClickListener(onClickListener)
        }
    }

    override fun getItemCount() = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameView: TextView = view.name
        val routeView: TextView = view.route
        val datetimeView: TextView = view.datetime
    }
}