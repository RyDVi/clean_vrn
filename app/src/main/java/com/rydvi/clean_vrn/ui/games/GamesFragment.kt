package com.rydvi.clean_vrn.ui.games

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.rydvi.clean_vrn.R
import com.rydvi.clean_vrn.api.Game
import com.rydvi.clean_vrn.ui.organizators.OrganizatorCreateEdit
import com.rydvi.clean_vrn.ui.utils.CreateEditMode
import kotlinx.android.synthetic.main.content_main.*


class GamesFragment : Fragment() {

    private lateinit var gamesViewModel: GamesViewModel
    private lateinit var swipeRefreshGames:SwipeRefreshLayout
    private lateinit var gameList:RecyclerView
    private var twoPane: Boolean = false


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_games, container, false)

        gamesViewModel =
            ViewModelProviders.of(this).get(GamesViewModel::class.java)

        //Отключение кнопки меню
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)


        gameList = root.findViewById(R.id.game_list)
        gamesViewModel.getGames()?.observe(this, Observer {
            setupRecyclerView(gameList, gamesViewModel.getGames())
        })

        val btnAddGame = root.findViewById<FloatingActionButton>(R.id.btn_add_game)
        btnAddGame.setOnClickListener {
            activity!!.findNavController(activity!!.nav_host_fragment.id)
                .navigate(R.id.nav_game_create_edit, Bundle().apply {
                    putString(GameCreateEditFragment.GAME_MODE, CreateEditMode.CREATE.getMode())
                })
        }

        swipeRefreshGames = root.findViewById(R.id.swipeRefreshGames)
        swipeRefreshGames.setOnRefreshListener { onRefreshGamesRecyclerView() }

        return root
    }

    private fun setupRecyclerView(recyclerView: RecyclerView, games: MutableLiveData<Array<Game>>) {
        games.value?.let {
            recyclerView.adapter =
                GameItemRecyclerViewAdapter(activity!!, games.value!!.toList(), twoPane)
        }
    }

    private fun onRefreshGamesRecyclerView(){
        gamesViewModel = ViewModelProviders.of(this).get(GamesViewModel::class.java)
        gamesViewModel.refreshGames()?.observe(this, Observer {
            swipeRefreshGames.isRefreshing = false
        })
    }
}