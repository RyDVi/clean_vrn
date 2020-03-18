package com.rydvi.clean_vrn.ui.organizators

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import androidx.navigation.findNavController
import com.rydvi.clean_vrn.R

import kotlinx.android.synthetic.main.activity_organizator_create_edit.*

class OrganizatorCreateEdit : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_organizator_create_edit)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val btnSave = findViewById<Button>(R.id.btn_organizator_save)
        btnSave.setOnClickListener {
            val intent = Intent(this, OrganizatorDetailActivity::class.java)
            startActivity(intent)
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

}
