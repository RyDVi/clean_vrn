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

    fun updateGame(
        id: Long,
        name: String,
        route: String,
        datetime: String,
        callback: (Game) -> Unit
    ) {
        DataRepository.updateGame(id, name, route, datetime, callback)
    }

    fun updateCoefficients(id: Long, callback: () -> Unit) {
        if (dataCoefficients !== null) {
            DataRepository.updateCoefficients(id, dataCoefficients!!.value!!, callback)
        } else {
            callback()
        }
    }

    fun createGame(
        name: String,
        route: String,
        datetime: String,
        callback: (Game) -> Unit
    ) {
        DataRepository.createGame(name, route, datetime, callback)
    }

    fun createCoefficients(id: Long, callback: (Array<Coefficient>?) -> Unit) {
        if (dataCoefficients !== null) {
            DataRepository.createCoefficients(id, dataCoefficients!!.value!!, callback)
        } else {
            callback(null)
        }
    }

    fun deleteGame(id: Long, callback: () -> Unit) {
        DataRepository.deleteGame(id, callback)
    }

}