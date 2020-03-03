package com.rydvi.clean_vrn.api

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter
import org.springframework.web.client.RestTemplate

class DataRepository {

    private val restTemplateJsonConverter: RestTemplate = {
        val restTemplate = RestTemplate()
        restTemplate.messageConverters.add(MappingJacksonHttpMessageConverter())
        restTemplate
    }()

    private val base_url = "http://192.168.0.57"

    fun getGames(): MutableLiveData<Array<Game>> {
        val gamesLive: MutableLiveData<Array<Game>> = MutableLiveData()
        Thread(Runnable {
            //Необходимо использовать postValue вместо "value =", поскольку только оно работает асинхронно
            gamesLive.postValue(
                restTemplateJsonConverter.getForObject(
                    "$base_url/games.php",
                    Array<Game>::class.java
                )
            )
        }).start()
        return gamesLive
    }

    fun getGames(callback: (Array<Game>) -> Unit) {
        Thread(Runnable {
            //Необходимо использовать postValue вместо "value =", поскольку только оно работает асинхронно
            callback(
                restTemplateJsonConverter.getForObject(
                    "$base_url/games.php",
                    Array<Game>::class.java
                )
            )
        }).start()
    }

    fun getGame(id: Int): MutableLiveData<Game> {
        val game: MutableLiveData<Game> = MutableLiveData()
        Thread(Runnable {
            game.postValue(
                restTemplateJsonConverter.getForObject(
                    "$base_url/games.php?id=$id",
                    Game::class.java
                )
            )
        })
        return game
    }
}