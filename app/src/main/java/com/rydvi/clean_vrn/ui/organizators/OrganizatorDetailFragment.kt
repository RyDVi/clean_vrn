package com.rydvi.clean_vrn.ui.organizators

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.rydvi.clean_vrn.R
import com.rydvi.clean_vrn.api.Organizator
import com.rydvi.clean_vrn.ui.organizators.dummy.DummyContent
import kotlinx.android.synthetic.main.activity_organizator_detail.*
import kotlinx.android.synthetic.main.organizator_detail.view.*
import kotlinx.android.synthetic.main.organizator_list_content.view.*

/**
 * A fragment representing a single Organizator detail screen.
 * This fragment is either contained in a [OrganizatorListActivity]
 * in two-pane mode (on tablets) or a [OrganizatorDetailActivity]
 * on handsets.
 */
class OrganizatorDetailFragment : Fragment() {

    /**
     * The dummy content this fragment is presenting.
     */
    private var organizator: Organizator? = null
    private lateinit var organizatorsViewModel: OrganizatorsViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.organizator_detail, container, false)

        organizatorsViewModel =
            ViewModelProviders.of(this).get(OrganizatorsViewModel::class.java)
        organizatorsViewModel.getOrganizators().observe(this, Observer {
            val organizators = it
            arguments?.let {
                if (it.containsKey(ARG_ITEM_ID)) {
                    val idOrganizator = it.getLong(ARG_ITEM_ID)
                    organizator = organizators.find { org -> org.id == idOrganizator }
                    activity?.toolbar_layout?.title =
                        activity!!.resources.getString(R.string.title_organizator_detail)+" ${organizator?.lastname}"

                    rootView.txt_organizator_lastname.text = organizator?.firstname
                    rootView.txt_organizator_firstname.text = organizator?.lastname
                    rootView.txt_organizator_middlename.text = organizator?.middlename
                    rootView.txt_organizator_email.text = organizator?.email
                    rootView.txt_organizator_phone.text = organizator?.phone
                }
            }
        })
        organizatorsViewModel.refreshOrganizators()

        return rootView
    }

    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val ARG_ITEM_ID = "item_id"
    }
}
