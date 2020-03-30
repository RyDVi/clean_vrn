package com.rydvi.clean_vrn.ui.games


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.rydvi.clean_vrn.R
import com.rydvi.clean_vrn.api.Game
import com.rydvi.clean_vrn.ui.utils.CreateEditMode
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.game_detail.view.*

/**
 * A fragment representing a single Game detail screen.
 * This fragment is either contained in a [GameListActivity]
 * in two-pane mode (on tablets) or a [GameDetailActivity]
 * on handsets.
 */
class GameDetailFragment : Fragment() {

    private var item: Game? = null
    private lateinit var gamesViewModel: GamesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.game_detail, container, false)

        gamesViewModel =
            ViewModelProviders.of(activity!!).get(GamesViewModel::class.java)

        gamesViewModel.getGames().observe(this, Observer {
            arguments?.let {
                if (it.containsKey(ARG_ITEM_ID)) {
                    val idGame = it.getLong(ARG_ITEM_ID)
                    item = gamesViewModel.getGames().value?.find { game ->
                        game.id == idGame
                    }
                    item?.let {
//                        activity?.toolbar_layout?.title =
//                            activity!!.resources.getString(R.string.title_game_detail) + " ${item?.name}"
                    }
                }
            }
            item?.let { game ->
                rootView.txt_game_address.text = game.route
                rootView.txt_game_datetime.text = game.datetime
                rootView.txt_game_route.text = game.route
                gamesViewModel.getCoefficients(game.id)?.observe(this, Observer {
                    setupRecyclerCoefficients(
                        rootView.recycler_game_detail_coefficients,
                        game.id
                    )
                })
            }
        })
        gamesViewModel.refreshGames()
        val btnEditGame = rootView.findViewById<FloatingActionButton>(R.id.btn_edit_game)
        btnEditGame.setOnClickListener {
            activity!!.findNavController(activity!!.nav_host_fragment.id)
                .navigate(R.id.gameCreateEditFragment, Bundle().apply {
                    putLong(GameCreateEditFragment.GAME_ID, item!!.id!!)
                    putString(GameCreateEditFragment.GAME_MODE, CreateEditMode.EDIT.getMode())
                })
        }

        val btnDeleteGame = rootView.findViewById<FloatingActionButton>(R.id.btn_delete_game)
        btnDeleteGame.setOnClickListener {
            gamesViewModel.deleteGame(item!!.id!!) {
                activity!!.runOnUiThread {
                    Toast.makeText(
                        activity,
                        resources.getString(R.string.msg_game_deleted),
                        Toast.LENGTH_LONG
                    ).show()
                }
                _navToGames()
            }
        }

        return rootView
    }

    private fun setupRecyclerCoefficients(recycler: RecyclerView, idGame: Long?) {
        val adapterGarbagesCoefficients =
            GarbageCoefficientItemRecyclerViewAdapter(
                activity!!,
                gamesViewModel.getCoefficients(idGame),
                CreateEditMode.READ
            )
        val linearLayoutManagerVertical = LinearLayoutManager(activity!!)
        linearLayoutManagerVertical.orientation = LinearLayoutManager.VERTICAL

        recycler.apply {
            layoutManager = linearLayoutManagerVertical
            adapter = adapterGarbagesCoefficients
        }
    }

    private fun _navToGames() {
        activity!!.runOnUiThread {
            activity!!.findNavController(activity!!.nav_host_fragment.id)
                .navigate(R.id.nav_games)
        }
    }

    companion object {
        const val ARG_ITEM_ID = "item_id"
    }
}
