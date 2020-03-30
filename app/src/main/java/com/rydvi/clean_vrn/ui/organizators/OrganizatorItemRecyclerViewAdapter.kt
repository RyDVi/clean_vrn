package com.rydvi.clean_vrn.ui.organizators

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.rydvi.clean_vrn.R
import com.rydvi.clean_vrn.api.Organizator
import com.rydvi.clean_vrn.ui.games.GameDetailFragment
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.organizator_list_content.view.*

class OrganizatorItemRecyclerViewAdapter(
    private val activity: FragmentActivity,
    private val values: List<Organizator>,
    private val twoPane: Boolean
) :
    RecyclerView.Adapter<OrganizatorItemRecyclerViewAdapter.ViewHolder>() {

    private val onClickListener: View.OnClickListener

    init {
        onClickListener = View.OnClickListener { v ->
            val item = v.tag as Organizator
            activity.findNavController(activity.nav_host_fragment.id)
                .navigate(R.id.nav_organizator_detail, Bundle().apply {
                    putLong(OrganizatorDetailFragment.ARG_ITEM_ID, item.id!!)
                })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.organizator_list_content, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.lastnameView.text = item.lastname
        holder.firstnameView.text = item.firstname
        holder.middlenameView.text = item.middlename

        with(holder.itemView) {
            tag = item
            setOnClickListener(onClickListener)
        }
    }

    override fun getItemCount() = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val lastnameView: TextView = view.organizator_lastname
        val firstnameView: TextView = view.organizator_firstname
        val middlenameView: TextView = view.organizator_middlename
    }
}