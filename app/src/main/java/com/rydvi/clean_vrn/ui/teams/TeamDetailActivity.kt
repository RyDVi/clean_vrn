package com.rydvi.clean_vrn.ui.teams

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import com.google.android.material.snackbar.Snackbar
import com.rydvi.clean_vrn.MainActivity
import com.rydvi.clean_vrn.R
import com.rydvi.clean_vrn.ui.organizators.OrganizatorCreateEdit
import com.rydvi.clean_vrn.ui.utils.CreateEditMode
import kotlinx.android.synthetic.main.activity_team_detail.*

/**
 * An activity representing a single Team detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a [TeamListActivity].
 */
class TeamDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_team_detail)
        setSupportActionBar(detail_toolbar)

        fab.setOnClickListener {
            val intent = Intent(this, TeamCreateEditActivity::class.java).apply {
                putExtra(
                    TeamCreateEditActivity.TEAM_ID,
                    intent.getLongExtra(TeamDetailFragment.ARG_ITEM_ID, 0)
                )
                putExtra(TeamCreateEditActivity.TEAM_MODE, CreateEditMode.EDIT)
            }
            //Отключение сохранения навигации в истории
            intent.flags = intent.flags or Intent.FLAG_ACTIVITY_NO_HISTORY
            startActivity(intent)
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (savedInstanceState == null) {
            val fragment = TeamDetailFragment().apply {
                arguments = Bundle().apply {
                    putLong(
                        TeamDetailFragment.ARG_ITEM_ID,
                        intent.getLongExtra(TeamDetailFragment.ARG_ITEM_ID, 0)
                    )
                }
            }

            supportFragmentManager.beginTransaction()
                .add(R.id.team_detail_container, fragment)
                .commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            android.R.id.home -> {
                val intent = Intent(this, MainActivity::class.java).apply {
                    putExtra(MainActivity.ARG_FRAGMENT_ID, R.id.nav_teams)
                }
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
}
