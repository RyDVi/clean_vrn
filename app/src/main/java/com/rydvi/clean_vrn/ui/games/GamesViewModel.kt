package com.rydvi.clean_vrn.ui.games

import android.app.Activity
import android.os.AsyncTask
import androidx.lifecycle.*
import com.rydvi.clean_vrn.R
import com.rydvi.clean_vrn.api.DataRepository
import com.rydvi.clean_vrn.api.Game
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter
import org.springframework.web.client.RestTemplate


class GamesViewModel : ViewModel() {

    private val dataRepository:DataRepository = DataRepository()
    private var dataGames: MutableLiveData<Array<Game>>? = null

    fun getGames(): MutableLiveData<Array<Game>> {
        if(dataGames==null){
            dataGames = MutableLiveData()
            dataRepository.getGames {
                dataGames?.postValue(it)
            }
        }
        return dataGames!!
    }

    fun refreshGames(): MutableLiveData<Array<Game>>? {
        dataRepository.getGames{
            dataGames?.postValue(it)
        }
        return dataGames
    }

}