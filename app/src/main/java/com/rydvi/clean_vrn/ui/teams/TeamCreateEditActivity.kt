package com.rydvi.clean_vrn.ui.teams

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import com.rydvi.clean_vrn.R
import com.rydvi.clean_vrn.ui.organizators.OrganizatorDetailActivity
import com.rydvi.clean_vrn.ui.organizators.OrganizatorListActivity

import kotlinx.android.synthetic.main.activity_team_create_edit.*

class TeamCreateEditActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_team_create_edit)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val btnSave = findViewById<Button>(R.id.btn_team_save)
        btnSave.setOnClickListener {
            val intent = Intent(this, TeamDetailActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            android.R.id.home -> {
                NavUtils.navigateUpTo(this, Intent(this, TeamListActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

}
