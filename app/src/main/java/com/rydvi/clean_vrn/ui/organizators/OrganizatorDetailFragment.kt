package com.rydvi.clean_vrn.ui.organizators

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.rydvi.clean_vrn.R
import com.rydvi.clean_vrn.api.Organizator
import kotlinx.android.synthetic.main.activity_organizator_detail.*
import kotlinx.android.synthetic.main.organizator_detail.view.*


/**
 * A fragment representing a single Organizator detail screen.
 * This fragment is either contained in a [OrganizatorListActivity]
 * in two-pane mode (on tablets) or a [OrganizatorDetailActivity]
 * on handsets.
 */
class OrganizatorDetailFragment : Fragment() {


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
                        activity!!.resources.getString(R.string.title_organizator_detail) + " ${organizator?.lastname}"

                    rootView.txt_organizator_lastname.text = organizator?.firstname
                    rootView.txt_organizator_firstname.text = organizator?.lastname
                    rootView.txt_organizator_middlename.text = organizator?.middlename
                    rootView.txt_organizator_email.text = organizator?.email
                    rootView.txt_organizator_phone.text = organizator?.phone

                    rootView.btn_generate_password.setOnClickListener {
                        acceptDialog {
                            organizatorsViewModel.generatePassword(organizator!!.id!!) { generatedPassword ->
                                showDialogGeneratedPassword(generatedPassword)
                            }
                        }
                    }
                }
            }
        })
        organizatorsViewModel.refreshOrganizators()

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

    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val ARG_ITEM_ID = "item_id"
    }
}
