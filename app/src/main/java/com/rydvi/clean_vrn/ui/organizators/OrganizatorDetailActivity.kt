package com.rydvi.clean_vrn.ui.organizators

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import android.view.MenuItem
import com.rydvi.clean_vrn.R
import kotlinx.android.synthetic.main.activity_organizator_detail.*

/**
 * An activity representing a single Organizator detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a [OrganizatorListActivity].
 */
class OrganizatorDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_organizator_detail)
        setSupportActionBar(detail_toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own detail action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (savedInstanceState == null) {
            val fragment = OrganizatorDetailFragment().apply {
                arguments = Bundle().apply {
                    putLong(
                        OrganizatorDetailFragment.ARG_ITEM_ID,
                        intent.getLongExtra(OrganizatorDetailFragment.ARG_ITEM_ID, 0)
                    )
                }
            }

            supportFragmentManager.beginTransaction()
                .add(R.id.organizator_detail_container, fragment)
                .commit()
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
