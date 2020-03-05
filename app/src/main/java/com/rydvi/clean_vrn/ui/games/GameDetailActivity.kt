package com.rydvi.clean_vrn.ui.games

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import com.google.android.material.snackbar.Snackbar
import com.rydvi.clean_vrn.R
import kotlinx.android.synthetic.main.activity_game_detail.*

/**
 * An activity representing a single Game detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a [GameListActivity].
 */
class GameDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_detail)
        setSupportActionBar(detail_toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own detail action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (savedInstanceState == null) {
            val fragment = GameDetailFragment().apply {
                arguments = Bundle().apply {
                    putLong(
                        GameDetailFragment.ARG_ITEM_ID,
                        intent.getLongExtra(GameDetailFragment.ARG_ITEM_ID, 0)
                    )
                }
            }

            supportFragmentManager.beginTransaction()
                .add(R.id.game_detail_container, fragment)
                .commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            android.R.id.home -> {
                NavUtils.navigateUpTo(this, Intent(this, GamesListActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
}
