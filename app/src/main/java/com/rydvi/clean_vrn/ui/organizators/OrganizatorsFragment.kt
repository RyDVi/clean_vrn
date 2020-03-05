package com.rydvi.clean_vrn.ui.organizators

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.rydvi.clean_vrn.R
import com.rydvi.clean_vrn.api.Organizator

class OrganizatorsFragment : Fragment() {

    private lateinit var organizatorsViewModel: OrganizatorsViewModel
    private var twoPane: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        organizatorsViewModel =
            ViewModelProviders.of(this).get(OrganizatorsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_gallery, container, false)
        organizatorsViewModel.getOrganizators().observe(this, Observer {
            val organzator_list = root.findViewById<RecyclerView>(R.id.organizator_list)
            setupRecyclerView(organzator_list, it)
        })
        return root
    }

    private fun setupRecyclerView(recyclerView: RecyclerView, organizators: Array<Organizator>) {
        recyclerView.adapter =
            OrganizatorItemRecyclerViewAdapter(activity!!, organizators.toList(), twoPane)
    }
}