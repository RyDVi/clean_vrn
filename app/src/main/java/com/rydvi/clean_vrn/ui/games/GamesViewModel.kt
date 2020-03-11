package com.rydvi.clean_vrn.ui.games

import android.util.Log
import androidx.lifecycle.*
import com.rydvi.clean_vrn.api.DataRepository
import com.rydvi.clean_vrn.api.Game


class GamesViewModel : ViewModel() {

    private val dataRepository: DataRepository = DataRepository
    private var dataGames: MutableLiveData<Array<Game>>? = null


    fun getGames(): MutableLiveData<Array<Game>> {
        if (dataGames == null) {
            dataGames = MutableLiveData()
            dataRepository.getGames {
                dataGames?.postValue(it)
            }
        }
        return dataGames!!
    }

    fun refreshGames(): MutableLiveData<Array<Game>>? {
        dataRepository.getGames {
            dataGames?.postValue(it)
        }
        return dataGames
    }

}