package com.rydvi.clean_vrn.ui.games

import android.os.AsyncTask
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rydvi.clean_vrn.R
import com.rydvi.clean_vrn.api.DataRepository
import com.rydvi.clean_vrn.api.Game
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter
import org.springframework.web.client.RestTemplate


class GamesViewModel : ViewModel() {





//    init{
//
//        Thread(Runnable {
//            //Получение одного значения
//            val url =  "http://192.168.0.57" + "/games.php?id=1"
//            val restTemplate = RestTemplate()
//            restTemplate.messageConverters.add(MappingJacksonHttpMessageConverter())
//            val result = restTemplate.getForObject(url,Game::class.java)
//
//            //Получение списка значений
//            val url =  "http://192.168.0.57" + "/games.php"
//            val restTemplate = RestTemplate()
//            restTemplate.messageConverters.add(MappingJacksonHttpMessageConverter())
//            val result = restTemplate.getForObject(url, Array<Game>::class.java)//проблема с преобразованием
//            val a = 1
//        }).start()
//    }

    private val dataRepository:DataRepository = DataRepository()
    private var dataGames: MutableLiveData<Array<Game>>? = null

    fun getGames(): MutableLiveData<Array<Game>> {
        if(dataGames==null){
            dataGames = dataRepository.getGames()
        }
        return dataGames!!
    }

    override fun onCleared() {
        super.onCleared()//В этом методе вы сможете выполнить все необходимые операции по освобождению ресурсов, закрытию соединений/потоков и т.п.
    }

}