package com.rydvi.clean_vrn.ui.games

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.rydvi.clean_vrn.MainActivity
import com.rydvi.clean_vrn.R
import com.rydvi.clean_vrn.api.Game
import com.rydvi.clean_vrn.ui.games.dummy.DummyContent


class GamesFragment : Fragment() {

    private lateinit var gamesViewModel: GamesViewModel
    private var twoPane: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        gamesViewModel =
            ViewModelProviders.of(this).get(GamesViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_tools, container, false)
//        val textView: TextView = root.findViewById(R.id.text_tools)
//        toolsViewModel.text.observe(this, Observer {
//            textView.text = it
//        })

        val game_list = root.findViewById<RecyclerView>(R.id.game_list)
        gamesViewModel.getGames()?.observe(this, Observer {
//            Log.e("Games getted", "games success getted")
            setupRecyclerView(game_list, gamesViewModel.getGames())
        })
        return root
    }

    private fun setupRecyclerView(recyclerView: RecyclerView, games: MutableLiveData<Array<Game>>) {
        recyclerView.adapter = GameItemRecyclerViewAdapter(activity!!, games.value!!.toList(), twoPane)
    }
}