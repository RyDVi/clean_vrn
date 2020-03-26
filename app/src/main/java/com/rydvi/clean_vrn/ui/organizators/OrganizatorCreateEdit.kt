package com.rydvi.clean_vrn.ui.organizators

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.rydvi.clean_vrn.R
import com.rydvi.clean_vrn.api.Organizator
import com.rydvi.clean_vrn.ui.teams.TeamCreateEditActivity
import com.rydvi.clean_vrn.ui.utils.CreateEditMode

import kotlinx.android.synthetic.main.activity_organizator_create_edit.*

class OrganizatorCreateEdit : AppCompatActivity() {

    private lateinit var orgsViewModel: OrganizatorsViewModel
    private lateinit var org: Organizator
    private lateinit var mode: CreateEditMode
    private lateinit var inpOrgLastname: EditText
    private lateinit var inpOrgFirstname: EditText
    private lateinit var inpOrgMiddlename: EditText
    private lateinit var inpOrgEmail: EditText
    private lateinit var inpOrgPhone: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_organizator_create_edit)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        inpOrgLastname = findViewById(R.id.inp_organizator_lastname)
        inpOrgFirstname = findViewById(R.id.inp_organizator_firstname)
        inpOrgMiddlename = findViewById(R.id.inp_organizator_middlename)
        inpOrgEmail = findViewById(R.id.inp_organizator_email)
        inpOrgPhone = findViewById(R.id.inp_organizator_phone)
        orgsViewModel = ViewModelProviders.of(this).get(OrganizatorsViewModel::class.java)
        mode = intent.extras!![ORG_MODE] as CreateEditMode

        if (mode === CreateEditMode.EDIT) {
            orgsViewModel.getOrganizators()?.observe(this, Observer { orgs ->
                val idOrg: Long = intent.getLongExtra(ORG_ID, 0)
                org = orgs.find { currentOrg -> currentOrg.id == idOrg }!!

                inpOrgLastname.setText(org.lastname)
                inpOrgFirstname.setText(org.firstname)
                inpOrgMiddlename.setText(org.middlename)
                inpOrgEmail.setText(org.email)
                inpOrgPhone.setText(org.phone)

            })
        }

        val btnSave = findViewById<Button>(R.id.btn_organizator_save)
        btnSave.setOnClickListener {
            if (mode === CreateEditMode.CREATE) {
                org = Organizator()
            }
            org.lastname = inpOrgLastname.text.toString()
            org.firstname = inpOrgFirstname.text.toString()
            org.middlename = inpOrgMiddlename.text.toString()
            org.email = inpOrgEmail.text.toString()
            org.phone = inpOrgPhone.text.toString()

            if (mode === CreateEditMode.EDIT) {
                orgsViewModel.updateOrganizator(org) {
                    _navToTeam(org.id!!)
                }
            } else {
                orgsViewModel.createOrganizator(org) { createdOrg ->
                    _navToTeam(createdOrg.id!!)
                }
            }

        }
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            android.R.id.home -> {
                NavUtils.navigateUpTo(this, Intent(this, OrganizatorListActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    private fun _navToTeam(idOrg: Long) {
        val intent = Intent(this, OrganizatorDetailActivity::class.java).apply {
            putExtra(
                OrganizatorDetailFragment.ARG_ITEM_ID,
                idOrg
            )
        }
        startActivity(intent)
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
