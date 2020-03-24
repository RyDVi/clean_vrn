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
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.rydvi.clean_vrn.R
import com.rydvi.clean_vrn.api.Game
import com.rydvi.clean_vrn.ui.organizators.OrganizatorCreateEdit
import com.rydvi.clean_vrn.ui.utils.CreateEditMode


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
        gamesViewModel =
            ViewModelProviders.of(this).get(GamesViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_games, container, false)

        //Отключение кнопки меню
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)


        gameList = root.findViewById(R.id.game_list)
        gamesViewModel.getGames()?.observe(this, Observer {
            setupRecyclerView(gameList, gamesViewModel.getGames())
        })

        val fab = activity!!.findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(activity, GameCreateEditActivity::class.java).apply {
                putExtra(GameCreateEditActivity.GAME_MODE, CreateEditMode.CREATE)
            }
            //Отключение сохранения навигации в истории
            intent.flags = intent.flags or Intent.FLAG_ACTIVITY_NO_HISTORY
            startActivity(intent)
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