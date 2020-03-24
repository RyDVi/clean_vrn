package com.rydvi.clean_vrn.ui.games

import androidx.lifecycle.*
import com.rydvi.clean_vrn.api.Coefficient
import com.rydvi.clean_vrn.api.DataRepository
import com.rydvi.clean_vrn.api.Game


class GamesViewModel : ViewModel() {

    private var dataGames: MutableLiveData<Array<Game>>? = null
    private var dataCoefficients: MutableLiveData<Array<Coefficient>>? = null


    fun getGames(): MutableLiveData<Array<Game>> {
        if (dataGames == null) {
            dataGames = MutableLiveData()
            DataRepository.getGames {
                dataGames?.postValue(it)
            }
        }
        return dataGames!!
    }

    fun refreshGames(): MutableLiveData<Array<Game>>? {
        DataRepository.getGames {
            dataGames?.postValue(it)
        }
        return dataGames
    }

    fun selectGame(id_game: Long, callback: (Game) -> Unit) {
        DataRepository.selectGame(id_game) { game ->
            callback(game)
        }
    }

    fun getCoefficients(idGame: Long?): MutableLiveData<Array<Coefficient>> {
        if (dataCoefficients == null) {
            dataCoefficients = MutableLiveData()
            DataRepository.getCoefficients(idGame) {
                dataCoefficients?.postValue(it)
            }
        }
        return dataCoefficients!!
    }

}