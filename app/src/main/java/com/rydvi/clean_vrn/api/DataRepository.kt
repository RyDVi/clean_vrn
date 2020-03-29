package com.rydvi.clean_vrn.api

import androidx.lifecycle.MutableLiveData
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter
import org.springframework.web.client.RestTemplate


object DataRepository {


    private const val base_url = "http://192.168.0.1"

    private var session: Session? = null
    public fun getSession() = session

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
            val headers = HttpHeaders()
            headers["Cookie"] = session?.idSession
            val entity = HttpEntity<String>(headers)
            callback(
                restTemplateJsonConverter.exchange(
                    "$base_url/organizators.php",
                    HttpMethod.GET,
                    entity,
                    Array<Organizator>::class.java
                ).body
            )
        }).start()
    }

    fun getCollectedGarbages(id_team: Long, callback: (Array<CollectedGarbage>) -> Unit?) {
        Thread(Runnable {
            val headers = HttpHeaders()
            headers["Content-Type"] = "application/json"
            headers["Cookie"] = session?.idSession
            val entity = HttpEntity<String>(headers)
            callback(
                restTemplateJsonConverter.exchange(
                    "$base_url/team_collected_garbages.php?id_team=$id_team",
                    HttpMethod.GET,
                    entity,
                    Array<CollectedGarbage>::class.java
                ).body
            )
        }).start()
    }

    fun getGarbages(callback: (Array<Garbage>) -> Unit?) {
        Thread(Runnable {
            val headers = HttpHeaders()
            headers["Content-Type"] = "application/json"
            headers["Cookie"] = session?.idSession
            val entity = HttpEntity<String>(headers)
            callback(
                restTemplateJsonConverter.exchange(
                    "$base_url/garbages.php",
                    HttpMethod.GET,
                    entity,
                    Array<Garbage>::class.java
                ).body
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

    fun logout(callbackSuccess: () -> Unit) = Thread(Runnable {
        val headers = HttpHeaders()
        headers["Cookie"] = session?.idSession
        val entity = HttpEntity<String>(headers)
        restTemplateJsonConverter.exchange(
            "$base_url/logout.php",
            HttpMethod.GET,
            entity,
            Session::class.java
        ).body
        session = null
        callbackSuccess()
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

    fun updateCollectedGarbages(
        id_team: Long,
        collectedGarbage: Array<CollectedGarbage>,
        callback: () -> Unit
    ) {
        Thread(Runnable {
            val headers = HttpHeaders()
            headers["Cookie"] = session?.idSession
            headers.add("Content-Type", "application/json")

            val bodyMap = LinkedHashMap<String, Any>()
            bodyMap["collected_garbages"] = collectedGarbage

            val requestEntity = HttpEntity(bodyMap, headers)
            restTemplateJsonConverter.exchange(
                "$base_url/team_collected_garbages.php?id_team=$id_team",
                HttpMethod.PUT,
                requestEntity,
                Session::class.java
            )
            callback()
        }).start()
    }

    fun updateTeam(id_team: Long, name: String, number: Long, callback: (team: Team) -> Unit) {
        Thread(Runnable {
            val headers = HttpHeaders()
            headers["Cookie"] = session?.idSession
            headers.add("Content-Type", "application/json")

            val bodyMap = LinkedHashMap<String, Any>()
            bodyMap["name"] = name
            bodyMap["number"] = number

            val requestEntity = HttpEntity(bodyMap, headers)
            callback(
                restTemplateJsonConverter.exchange(
                    "$base_url/teams.php?id_team=$id_team",
                    HttpMethod.PUT,
                    requestEntity,
                    Team::class.java
                ).body
            )

        }).start()
    }

    fun createTeam(name: String, number: Long, callback: (team: Team) -> Unit) {
        Thread(Runnable {
            val headers = HttpHeaders()
            headers["Cookie"] = session?.idSession
            headers.add("Content-Type", "application/json")

            val bodyMap = LinkedHashMap<String, Any>()
            bodyMap["name"] = name
            bodyMap["number"] = number

            val requestEntity = HttpEntity(bodyMap, headers)
            callback(
                restTemplateJsonConverter.exchange(
                    "$base_url/teams.php",
                    HttpMethod.POST,
                    requestEntity,
                    Team::class.java
                ).body
            )

        }).start()
    }

    fun getCoefficients(id_game: Long?, callback: (Array<Coefficient>) -> Unit?) {
        Thread(Runnable {
            val headers = HttpHeaders()
            headers["Cookie"] = session?.idSession
            headers.add("Content-Type", "application/json")

            val entity = HttpEntity<String>(headers)
            val coefficients = restTemplateJsonConverter.exchange(
                "$base_url/garbages_coefficients.php?id_game=$id_game",
                HttpMethod.GET,
                entity,
                Array<Coefficient>::class.java
            ).body
            callback(coefficients)
        }).start()
    }

    fun updateGame(
        id: Long,
        name: String,
        route: String,
        datetime: String,
        callback: (Game) -> Unit
    ) {
        Thread(Runnable {
            val headers = HttpHeaders()
            headers["Cookie"] = session?.idSession
            headers.add("Content-Type", "application/json")

            val bodyMap = LinkedHashMap<String, Any>()
            bodyMap["name"] = name
            bodyMap["route"] = route
            bodyMap["datetime"] = datetime

            val entity = HttpEntity(bodyMap, headers)
            val game = restTemplateJsonConverter.exchange(
                "$base_url/games.php?id_game=$id",
                HttpMethod.PUT,
                entity,
                Game::class.java
            ).body
            callback(game)
        }).start()
    }

    fun updateCoefficients(id: Long, coefficients: Array<Coefficient>, callback: () -> Unit) {
        Thread(Runnable {
            val headers = HttpHeaders()
            headers["Cookie"] = session?.idSession
            headers.add("Content-Type", "application/json")

            val bodyMap = LinkedHashMap<String, Any>()
            bodyMap["coefficients"] = coefficients

            val entity = HttpEntity(bodyMap, headers)
            restTemplateJsonConverter.exchange(
                "$base_url/garbages_coefficients.php?id_game=$id",
                HttpMethod.PUT,
                entity,
                Session::class.java
            ).body
            callback()
        }).start()
    }

    fun createGame(name: String, route: String, datetime: String, callback: (Game) -> Unit) {
        Thread(Runnable {
            val headers = HttpHeaders()
            headers["Cookie"] = session?.idSession
            headers.add("Content-Type", "application/json")

            val bodyMap = LinkedHashMap<String, Any>()
            bodyMap["name"] = name
            bodyMap["route"] = route
            bodyMap["datetime"] = datetime

            val entity = HttpEntity(bodyMap, headers)
            val game = restTemplateJsonConverter.exchange(
                "$base_url/games.php",
                HttpMethod.POST,
                entity,
                Game::class.java
            ).body
            callback(game)
        }).start()
    }

    fun createCoefficients(
        id: Long, coefficients: Array<Coefficient>,
        callback: (Array<Coefficient>?) -> Unit
    ) {
        Thread(Runnable {
            val headers = HttpHeaders()
            headers["Cookie"] = session?.idSession
            headers.add("Content-Type", "application/json")

            val bodyMap = LinkedHashMap<String, Any>()
            bodyMap["coefficients"] = coefficients

            val entity = HttpEntity(bodyMap, headers)
            val createdCoefficients = restTemplateJsonConverter.exchange(
                "$base_url/garbages_coefficients.php?id_game=$id",
                HttpMethod.POST,
                entity,
                Array<Coefficient>::class.java
            ).body
            callback(createdCoefficients)
        }).start()
    }

    fun updateOrganizator(org: Organizator, callback: () -> Unit) = Thread(Runnable {
        val headers = HttpHeaders()
        headers["Cookie"] = session?.idSession
        headers.add("Content-Type", "application/json")

        val bodyMap = LinkedHashMap<String, Any>()
        bodyMap["lastname"] = org.lastname!!
        bodyMap["firstname"] = org.firstname!!
        bodyMap["middlename"] = org.middlename!!
        bodyMap["email"] = org.email!!
        bodyMap["phone"] = org.phone!!

        val entity = HttpEntity(bodyMap, headers)
        restTemplateJsonConverter.exchange(
            "$base_url/organizators.php?id=${org.id}",
            HttpMethod.PUT,
            entity,
            Session::class.java
        ).body
        callback()
    }).start()

    fun createOrganizator(org: Organizator, callback: (Organizator) -> Unit) = Thread(Runnable {
        val headers = HttpHeaders()
        headers["Cookie"] = session?.idSession
        headers.add("Content-Type", "application/json")

        val bodyMap = LinkedHashMap<String, Any>()
        bodyMap["lastname"] = org.lastname!!
        bodyMap["firstname"] = org.firstname!!
        bodyMap["middlename"] = org.middlename!!
        bodyMap["email"] = org.email!!
        bodyMap["phone"] = org.phone!!

        val entity = HttpEntity(bodyMap, headers)
        val createdCoefficients = restTemplateJsonConverter.exchange(
            "$base_url/organizators.php",
            HttpMethod.POST,
            entity,
            Organizator::class.java
        ).body
        callback(createdCoefficients)
    }).start()

    fun generatePassword(id: Long, callback: (String) -> Unit) = Thread(Runnable {
        val headers = HttpHeaders()
        headers["Cookie"] = session?.idSession
        val entity = HttpEntity<String>(headers)
        val generatedPassword = restTemplateJsonConverter.exchange(
            "$base_url/generate_org_password.php?id=$id",
            HttpMethod.GET,
            entity,
            Password::class.java
        ).body
        callback(generatedPassword.password!!)

    }).start()
}