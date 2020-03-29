package com.rydvi.clean_vrn.ui.games

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.rydvi.clean_vrn.MainActivity
import com.rydvi.clean_vrn.R
import com.rydvi.clean_vrn.ui.organizators.OrganizatorCreateEdit
import com.rydvi.clean_vrn.ui.utils.CreateEditMode
import kotlinx.android.synthetic.main.activity_game_detail.*

/**
 * An activity representing a single Game detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a [GameListActivity].
 */
class GameDetailActivity : AppCompatActivity() {
    private lateinit var gamesViewModel: GamesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_detail)
        setSupportActionBar(detail_toolbar)

        gamesViewModel =
            ViewModelProviders.of(this).get(GamesViewModel::class.java)

        fab.setOnClickListener {
            val intent = Intent(this, GameCreateEditActivity::class.java).apply {
                putExtra(
                    GameCreateEditActivity.GAME_ID,
                    intent.getLongExtra(GameDetailFragment.ARG_ITEM_ID, 0)
                )
                putExtra(GameCreateEditActivity.GAME_MODE, CreateEditMode.EDIT)
            }
            //Отключение сохранения навигации в истории
            intent.flags = intent.flags or Intent.FLAG_ACTIVITY_NO_HISTORY
            startActivity(intent)
        }

        btn_remove_game.setOnClickListener {
            gamesViewModel.deleteGame(intent.getLongExtra(GameDetailFragment.ARG_ITEM_ID, 0)) {
                runOnUiThread{
                    Toast.makeText(
                        applicationContext,
                        resources.getString(R.string.msg_game_deleted),
                        Toast.LENGTH_LONG
                    ).show()
                }
                _navToGames()
            }
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
//                NavUtils.navigateUpTo(this, Intent(this, GamesListActivity::class.java))
                //Передача ИД фрагмента для навигации на него в MainActivity
                _navToGames()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    private fun _navToGames(){
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra(MainActivity.ARG_FRAGMENT_ID, R.id.nav_games)
        }
        startActivity(intent)
    }
}
