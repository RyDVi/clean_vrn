package com.rydvi.clean_vrn.ui.organizators

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.rydvi.clean_vrn.R
import com.rydvi.clean_vrn.api.Organizator
import com.rydvi.clean_vrn.ui.utils.CreateEditMode


class OrganizatorsFragment : Fragment() {

    private lateinit var organizatorsViewModel: OrganizatorsViewModel
    private var twoPane: Boolean = false
    private lateinit var swipeRefreshOrganizators:SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        organizatorsViewModel =
            ViewModelProviders.of(this).get(OrganizatorsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_organizators, container, false)
        organizatorsViewModel.getOrganizators().observe(this, Observer {
            val organzator_list = root.findViewById<RecyclerView>(R.id.organizator_list)
            setupRecyclerView(organzator_list, it)
        })

        val fab = activity!!.findViewById<FloatingActionButton>(R.id.btn_team_edit)
        fab.setOnClickListener {
            val intent = Intent(activity, OrganizatorCreateEdit::class.java).apply {
                putExtra(OrganizatorCreateEdit.ORG_MODE, CreateEditMode.CREATE)
            }
            //Отключение сохранения навигации в истории
            intent.flags = intent.flags or Intent.FLAG_ACTIVITY_NO_HISTORY
            startActivity(intent)
        }

        swipeRefreshOrganizators = root.findViewById(R.id.swipeRefreshOrganizators)
        swipeRefreshOrganizators.setOnRefreshListener { onRefreshOrganizatorsRecyclerView() }

        return root
    }

    private fun setupRecyclerView(recyclerView: RecyclerView, organizators: Array<Organizator>) {
        recyclerView.adapter =
            OrganizatorItemRecyclerViewAdapter(activity!!, organizators.toList(), twoPane)
    }

    private fun onRefreshOrganizatorsRecyclerView(){
        organizatorsViewModel = ViewModelProviders.of(this).get(OrganizatorsViewModel::class.java)
        organizatorsViewModel.refreshOrganizators()?.observe(this, Observer {
            swipeRefreshOrganizators.isRefreshing = false
        })
    }
}