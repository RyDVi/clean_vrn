package com.rydvi.clean_vrn.api

import androidx.lifecycle.MutableLiveData
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter
import org.springframework.web.client.RestTemplate


object DataRepository {


    private const val base_url = "http://192.168.0.57"
    //    private const val base_url = "http://192.168.0.103"
    private var session: Session? = null

    private val restTemplateJsonConverter: RestTemplate = {
        val restTemplate = RestTemplate()
        restTemplate.messageConverters.add(MappingJacksonHttpMessageConverter())
        restTemplate
    }()

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
        }).start()
        return game
    }

    fun selectGame(id_game: Long, callback: (Game) -> Unit) {
        Thread(Runnable {
            val headers = HttpHeaders()
            headers["Content-Type"] = "application/json"
            headers["Cookie"] = session?.idSession
            val bodyMap = LinkedHashMap<String, Long>()
            bodyMap["id_game"] = id_game
            val requestEntity = HttpEntity(bodyMap, headers)
            callback(
                restTemplateJsonConverter.postForObject(
                    "$base_url/select_game.php",
                    requestEntity,
                    Game::class.java
                )
            )
        }).start()
    }

    fun getTeams(callback: (Array<Team>) -> Unit) = Thread(Runnable {
        val headers = HttpHeaders()
        headers["Cookie"] = session?.idSession
        val entity = HttpEntity<String>(headers)
        callback(
            restTemplateJsonConverter.exchange(
                "$base_url/teams.php",
                HttpMethod.GET,
                entity,
                Array<Team>::class.java
            ).body
        )
    }).start()

    fun getOrganizators(callback: (Array<Organizator>) -> Unit?) {
        Thread(Runnable {
            callback(

                restTemplateJsonConverter.getForObject(
                    "$base_url/organizators.php",
                    Array<Organizator>::class.java
                )
            )
        }).start()
    }

    fun login(username: String, password: String, isPlayer: Boolean, callback: (Session) -> Unit) =
        Thread(Runnable {
            val headers = HttpHeaders()
//        headers.add("Content-Type", "application/json")

            //Для массивов. Например, в username может быть несколько
//        val bodyMap: MultiValueMap<String, String> = LinkedMultiValueMap()
//        bodyMap.add("username", username)
//        bodyMap.add("password", password)
            val bodyMap = LinkedHashMap<String, Any>()
            if (isPlayer) {
                bodyMap["is_player"] = true
            } else {
                bodyMap["username"] = username
                bodyMap["password"] = password
            }
            val requestEntity = HttpEntity(bodyMap, headers)
            session = restTemplateJsonConverter.postForObject(
                "$base_url/login.php",
                requestEntity,
                Session::class.java
            )
            callback(session!!)
        }).start()

    fun logout(callback: (Session) -> Unit) = Thread(Runnable {
        restTemplateJsonConverter.getForObject(
            "$base_url/logout.php",
            Session::class.java
        )
    }).start()

    fun testSession(callback: (Session) -> Unit) = Thread(Runnable {
        val headers = HttpHeaders()
        headers["Cookie"] = session?.idSession
        val entity = HttpEntity<String>(headers)
        val testSession = restTemplateJsonConverter.exchange(
            "$base_url/test_session.php",
            HttpMethod.GET,
            entity,
            Session::class.java
        )
        callback(testSession.body)
    }).start()
}