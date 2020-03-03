package com.rydvi.clean_vrn.api

import androidx.lifecycle.MutableLiveData
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
//            val games = restTemplateJsonConverter.getForObject(
//                "$base_url/games.php",
//                Array<Game>::class.java
//            )
//            for (game in games) {
//                gamesLive.postValue(game)
//            }
            gamesLive.postValue(restTemplateJsonConverter.getForObject(
                "$base_url/games.php",
                Array<Game>::class.java))
        }).start()
        return gamesLive
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