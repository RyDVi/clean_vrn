package com.rydvi.clean_vrn.ui.organizators


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.rydvi.clean_vrn.MainActivity
import com.rydvi.clean_vrn.R
import com.rydvi.clean_vrn.api.Organizator
import com.rydvi.clean_vrn.ui.games.GameDetailFragment
import com.rydvi.clean_vrn.ui.utils.CreateEditMode
import com.rydvi.clean_vrn.ui.utils.getCreateEditModeByString
import kotlinx.android.synthetic.main.content_main.*

/**
 * A simple [Fragment] subclass.
 */
class OragnizatorCreateEditFragment : Fragment() {

    private lateinit var orgsViewModel: OrganizatorsViewModel
    private lateinit var org: Organizator
    private lateinit var editMode: CreateEditMode
    private lateinit var inpOrgLastname: EditText
    private lateinit var inpOrgFirstname: EditText
    private lateinit var inpOrgMiddlename: EditText
    private lateinit var inpOrgEmail: EditText
    private lateinit var inpOrgPhone: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_organizator_create_edit, container, false)
        (activity as MainActivity).showLoading(true)

        inpOrgLastname = rootView.findViewById(R.id.inp_organizator_lastname)
        inpOrgFirstname = rootView.findViewById(R.id.inp_organizator_firstname)
        inpOrgMiddlename = rootView.findViewById(R.id.inp_organizator_middlename)
        inpOrgEmail = rootView.findViewById(R.id.inp_organizator_email)
        inpOrgPhone = rootView.findViewById(R.id.inp_organizator_phone)
        orgsViewModel = ViewModelProviders.of(this).get(OrganizatorsViewModel::class.java)
        editMode = getCreateEditModeByString(arguments!!.getString(ORG_MODE)!!)

        if (editMode === CreateEditMode.EDIT) {
            orgsViewModel.getOrganizators()?.observe(this, Observer { orgs ->
                val idOrg: Long = arguments!!.getLong(ORG_ID, 0)
                org = orgs.find { currentOrg -> currentOrg.id == idOrg }!!

                inpOrgLastname.setText(org.lastname)
                inpOrgFirstname.setText(org.firstname)
                inpOrgMiddlename.setText(org.middlename)
                inpOrgEmail.setText(org.email)
                inpOrgPhone.setText(org.phone)

                (activity as MainActivity).showLoading(false)
            })
        } else {
            (activity as MainActivity).showLoading(false)
        }

        val btnOrgSave = rootView.findViewById<FloatingActionButton>(R.id.btn_organizator_save)
        btnOrgSave.setOnClickListener {
            (activity as MainActivity).showLoading(true)
            if (editMode === CreateEditMode.CREATE) {
                org = Organizator()
            }
            org.lastname = inpOrgLastname.text.toString()
            org.firstname = inpOrgFirstname.text.toString()
            org.middlename = inpOrgMiddlename.text.toString()
            org.email = inpOrgEmail.text.toString()
            org.phone = inpOrgPhone.text.toString()

            if (editMode === CreateEditMode.EDIT) {
                orgsViewModel.updateOrganizator(org) {
                    _navToTeam(org.id!!)
                }
            } else {
                orgsViewModel.createOrganizator(org) { createdOrg ->
                    _navToTeam(createdOrg.id!!)
                }
            }

        }
        
        return rootView
    }

    private fun _navToTeam(idOrg: Long) {
        activity!!.runOnUiThread {
            activity!!.findNavController(activity!!.nav_host_fragment.id)
                .navigate(
                    R.id.nav_organizator_detail, Bundle().apply {
                        putLong(OrganizatorDetailFragment.ARG_ITEM_ID, idOrg)
                    }, NavOptions.Builder()
                        .setPopUpTo(
                            R.id.nav_organizators,
                            true
                        ).build()
                )
        }
    }

    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val ORG_ID = "organizator_id"
        const val ORG_MODE = "organizator_create_edit_mode"
    }
}
