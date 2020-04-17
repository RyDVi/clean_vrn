package com.rydvi.clean_vrn.ui.organizators

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.rydvi.clean_vrn.MainActivity
import com.rydvi.clean_vrn.R
import com.rydvi.clean_vrn.api.DataRepository
import com.rydvi.clean_vrn.api.Organizator
import com.rydvi.clean_vrn.ui.utils.CreateEditMode
import com.rydvi.clean_vrn.ui.utils.GameStatus
import com.rydvi.clean_vrn.ui.utils.isAdmin
import com.rydvi.clean_vrn.ui.utils.isPlayer
import kotlinx.android.synthetic.main.content_main.*


class OrganizatorsFragment : Fragment() {

    private lateinit var organizatorsViewModel: OrganizatorsViewModel
    private var twoPane: Boolean = false
    private lateinit var swipeRefreshOrganizators: SwipeRefreshLayout

    private lateinit var btnOrgAdd: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_organizators, container, false)

        organizatorsViewModel =
            ViewModelProviders.of(this).get(OrganizatorsViewModel::class.java)
        organizatorsViewModel.getOrganizators().observe(this, Observer {
            val organzatorList = root.findViewById<RecyclerView>(R.id.organizator_list)
            setupRecyclerView(organzatorList, it)
        })

        btnOrgAdd = root.findViewById(R.id.btn_organizator_add)
        btnOrgAdd.setOnClickListener {
            activity!!.findNavController(activity!!.nav_host_fragment.id)
                .navigate(R.id.nav_organizator_create_edit, Bundle().apply {
                    putString(
                        OragnizatorCreateEditFragment.ORG_MODE,
                        CreateEditMode.CREATE.getMode()
                    )
                })
        }

        organizatorsViewModel.refreshOrganizators()

        swipeRefreshOrganizators = root.findViewById(R.id.swipeRefreshOrganizators)
        swipeRefreshOrganizators.setOnRefreshListener { onRefreshOrganizatorsRecyclerView() }

        toggleButtons()
        return root
    }

    private fun setupRecyclerView(recyclerView: RecyclerView, organizators: Array<Organizator>) {
        organizators.sortWith(
            compareBy(
                { it.lastname?.toUpperCase() },
                { it.firstname?.toUpperCase() },
                { it.middlename?.toUpperCase() })
        )
        recyclerView.adapter =
            OrganizatorItemRecyclerViewAdapter(activity!!, organizators.toList(), twoPane)
    }

    private fun onRefreshOrganizatorsRecyclerView() {
        organizatorsViewModel = ViewModelProviders.of(this).get(OrganizatorsViewModel::class.java)
        organizatorsViewModel.refreshOrganizators()?.observe(this, Observer {
            swipeRefreshOrganizators.isRefreshing = false
        })
    }

    fun toggleButtons(){
        DataRepository.selectedGame?.let {
            if(it.id_status!== GameStatus.completed.getTypeId()){
                if (!isAdmin()) btnOrgAdd.hide() else btnOrgAdd.show()
            } else {
                btnOrgAdd.hide()
            }
        }?:run  {
            btnOrgAdd.hide()
        }
    }
}