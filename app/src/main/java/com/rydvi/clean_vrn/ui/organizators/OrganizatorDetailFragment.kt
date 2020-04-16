package com.rydvi.clean_vrn.ui.organizators

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.rydvi.clean_vrn.R
import com.rydvi.clean_vrn.api.DataRepository
import com.rydvi.clean_vrn.api.Organizator
import com.rydvi.clean_vrn.ui.utils.CreateEditMode
import com.rydvi.clean_vrn.ui.utils.GameStatus
import com.rydvi.clean_vrn.ui.utils.isAdmin
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*


class OrganizatorDetailFragment : Fragment() {


    private var organizator: Organizator? = null
    private lateinit var orgsViewModel: OrganizatorsViewModel

    private lateinit var btnOrgDelete: FloatingActionButton
    private lateinit var btnOrgGenPassword: Button
    private lateinit var btnOrgEdit: FloatingActionButton


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_organizator_detail, container, false)

        orgsViewModel =
            ViewModelProviders.of(this).get(OrganizatorsViewModel::class.java)
        orgsViewModel.getOrganizators().observe(this, Observer {
            val organizators = it
            arguments?.let {
                if (it.containsKey(ARG_ITEM_ID)) {
                    val idOrganizator = it.getLong(ARG_ITEM_ID)
                    organizator = organizators.find { org -> org.id == idOrganizator }
                    activity?.toolbar?.title =
                        activity!!.resources.getString(R.string.title_organizator_detail) + " ${organizator?.lastname}"

                    rootView.findViewById<TextView>(R.id.txt_organizator_lastname).text =
                        organizator?.lastname
                    rootView.findViewById<TextView>(R.id.txt_organizator_firstname).text =
                        organizator?.firstname
                    rootView.findViewById<TextView>(R.id.txt_organizator_middlename).text =
                        organizator?.middlename
                    rootView.findViewById<TextView>(R.id.txt_organizator_email).text =
                        organizator?.email
                    rootView.findViewById<TextView>(R.id.txt_organizator_phone).text =
                        organizator?.phone
                }
            }
        })
        orgsViewModel.refreshOrganizators()

        btnOrgEdit = rootView.findViewById(R.id.btn_organizator_edit)
        btnOrgEdit.setOnClickListener {
            activity!!.findNavController(activity!!.nav_host_fragment.id)
                .navigate(R.id.nav_organizator_create_edit, Bundle().apply {
                    putLong(OragnizatorCreateEditFragment.ORG_ID, organizator!!.id!!)
                    putString(OragnizatorCreateEditFragment.ORG_MODE, CreateEditMode.EDIT.getMode())
                })
        }

        btnOrgDelete = rootView.findViewById(R.id.btn_organizator_delete)
        btnOrgDelete.setOnClickListener {
            orgsViewModel.deleteOrganizator(organizator!!.id!!) {
                activity!!.runOnUiThread {
                    Toast.makeText(
                        activity,
                        resources.getString(R.string.msg_org_deleted),
                        Toast.LENGTH_LONG
                    ).show()
                }
                _navToOrgs()
            }
        }

        btnOrgGenPassword = rootView.findViewById(R.id.btn_generate_password)
        btnOrgGenPassword.setOnClickListener {
            acceptDialog {
                orgsViewModel.generatePassword(organizator!!.id!!) { generatedPassword ->
                    showDialogGeneratedPassword(generatedPassword)
                    activity!!.runOnUiThread {
                    }
                }
            }
        }
        toggleButtons()
        return rootView
    }

    private fun acceptDialog(callbackAccept: () -> Unit) {
        val builderAccept: AlertDialog.Builder =
            AlertDialog.Builder(activity!!)
        builderAccept.setMessage(
            activity!!.resources.getString(R.string.dlg_accept_generate_password)
        )
            .setPositiveButton(activity!!.resources.getString(R.string.dlg_accept_generate_password_btn_ok)) { _, _ ->
                callbackAccept()
            }
            .setNegativeButton(activity!!.resources.getString(R.string.dlg_accept_generate_password_btn_not)) { _, _ ->

            }
        activity!!.runOnUiThread {
            builderAccept.create().show()
        }
    }

    private fun showDialogGeneratedPassword(generatedPassword: String) {
        val builder: AlertDialog.Builder =
            AlertDialog.Builder(activity!!)
        builder.setMessage(activity!!.resources.getString(R.string.dlg_save_generated_password) + ": $generatedPassword")
            .setPositiveButton(activity!!.resources.getString(R.string.dlg_save_generated_password_btn_ok)) { _, _ ->
            }
        activity!!.runOnUiThread {
            builder.create().show()
        }
    }

    fun toggleButtons() {
        DataRepository.selectedGame?.let {
            if (it.id_status !== GameStatus.completed.getTypeId()) {
                if (!isAdmin()) {
                    hideAllButtons()
                } else {
                    btnOrgDelete.show()
                    btnOrgEdit.show()
                    btnOrgGenPassword.visibility = View.VISIBLE
                }
            } else {
                hideAllButtons()
            }
        }?:run  {
            hideAllButtons()
        }
    }

    fun hideAllButtons() {
        btnOrgDelete.hide()
        btnOrgEdit.hide()
        btnOrgGenPassword.visibility = View.GONE
    }

    private fun _navToOrgs() {
        activity!!.runOnUiThread {
            activity!!.findNavController(activity!!.nav_host_fragment.id)
                .navigate(R.id.nav_organizators)
        }
    }

    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val ARG_ITEM_ID = "item_id"
    }
}
