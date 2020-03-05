package com.rydvi.clean_vrn.ui.teams

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import com.google.android.material.snackbar.Snackbar
import com.rydvi.clean_vrn.R
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

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own detail action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
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
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back

                NavUtils.navigateUpTo(this, Intent(this, TeamListActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
}
