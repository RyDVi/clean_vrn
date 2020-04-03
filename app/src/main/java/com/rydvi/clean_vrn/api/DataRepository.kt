package com.rydvi.clean_vrn.api

import android.app.Activity
import android.widget.ProgressBar
import com.rydvi.clean_vrn.MainActivity
import com.rydvi.clean_vrn.ui.login.LoginActivity
import kotlinx.android.synthetic.main.activity_login.*
import org.codehaus.jackson.map.ObjectMapper
import org.springframework.http.*
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate


object DataRepository {
    private const val base_url = "http://192.168.0.1"

    private var session: Session? = null
    fun getSession() = session

    var activity: Activity? = null


    private val restTemplateJsonConverter: RestTemplate = {
        val restTemplate = RestTemplate()
        restTemplate.messageConverters.add(MappingJacksonHttpMessageConverter())
        restTemplate
    }()

    fun getGames(callback: (Array<Game>) -> Unit, callbackFailed: (Error) -> Unit) = sendRequest(
        "games.php",
        HttpMethod.GET,
        null,
        Array<Game>::class.java,
        callback,
        callbackFailed
    )

    fun selectGame(id_game: Long, callback: (Game) -> Unit) {
        Thread(Runnable {
            val headers = HttpHeaders()
            headers["Content-Type"] = MediaType.APPLICATION_JSON_VALUE
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
            headers["Content-Type"] = MediaType.APPLICATION_JSON_VALUE
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
            headers["Content-Type"] = MediaType.APPLICATION_JSON_VALUE
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

    fun login(
        username: String, password: String, isPlayer: Boolean, callbackSuccess: (Session) -> Unit
        , callbackFailed: (Error) -> Unit
    ) {
        val bodyMap = LinkedHashMap<String, Any>()
        if (isPlayer) {
            bodyMap["is_player"] = true
        } else {
            bodyMap["username"] = username
            bodyMap["password"] = password
        }
        sendRequest(
            "login.php",
            HttpMethod.POST,
            bodyMap,
            Session::class.java,
            { createdSession ->
                session = createdSession
                callbackSuccess(createdSession)
            },
            callbackFailed
        )
    }


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

    fun updateCollectedGarbages(
        id_team: Long,
        collectedGarbage: Array<CollectedGarbage>,
        callback: () -> Unit
    ) {
        Thread(Runnable {
            val headers = HttpHeaders()
            headers["Cookie"] = session?.idSession
            headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE)

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
            headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE)

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
            headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE)

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
            headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE)

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
            headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE)

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
            headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE)

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
            headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE)

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
            headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE)

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
        headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE)

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
        headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE)

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

    fun deleteGame(id: Long, callback: () -> Unit) = Thread(Runnable {
        val headers = HttpHeaders()
        headers["Cookie"] = session?.idSession
        val entity = HttpEntity<String>(headers)
        val deleteStatus = restTemplateJsonConverter.exchange(
            "$base_url/games.php?id_game=$id",
            HttpMethod.DELETE,
            entity,
            Void::class.java
        ).statusCode
        when (deleteStatus) {
            HttpStatus.OK -> {
                callback()
            }
            HttpStatus.NOT_FOUND -> {
                callback()
            }
            else -> {
                callback()
            }
        }
    }).start()

    fun deleteOrganizator(id: Long, callback: () -> Unit) = Thread(Runnable {
        val headers = HttpHeaders()
        headers["Cookie"] = session?.idSession
        val entity = HttpEntity<String>(headers)
        val deleteStatus = restTemplateJsonConverter.exchange(
            "$base_url/organizators.php?id=$id",
            HttpMethod.DELETE,
            entity,
            Void::class.java
        ).statusCode
        when (deleteStatus) {
            HttpStatus.OK -> {
                callback()
            }
            HttpStatus.NOT_FOUND -> {
                callback()
            }
            else -> {
                callback()
            }
        }
    }).start()

    fun deleteTeam(id: Long, callback: () -> Unit) = Thread(Runnable {
        val headers = HttpHeaders()
        headers["Cookie"] = session?.idSession
        val entity = HttpEntity<String>(headers)
        val deleteStatus = restTemplateJsonConverter.exchange(
            "$base_url/teams.php?id=$id",
            HttpMethod.DELETE,
            entity,
            Void::class.java
        ).statusCode
        when (deleteStatus) {
            HttpStatus.OK -> {
                callback()
            }
            HttpStatus.NOT_FOUND -> {
                callback()
            }
            else -> {
                callback()
            }
        }
    }).start()

    fun completeTheGame(id: Long, callbackSuccess: () -> Unit, callbackFailed: (Error) -> Unit) =
        Thread(Runnable {
            val headers = HttpHeaders()
            headers["Cookie"] = session?.idSession
            val entity = HttpEntity<String>(headers)
            var error: Error? = null
            var statusCode: HttpStatus
            try {
                val response = restTemplateJsonConverter.exchange(
                    "$base_url/games_control.php?id=$id",
                    HttpMethod.GET,
                    entity,
                    Void::class.java
                )
                statusCode = response.statusCode
            } catch (ex: HttpClientErrorException) {
                statusCode = ex.statusCode
                error = getErrorByEx(ex)
            }
            if (statusCode === HttpStatus.OK) {
                callbackSuccess()
            } else {
                callbackFailed(error!!)
            }
        }).start()

    fun <T> sendRequest(
        scriptNameWithParams: String,
        method: HttpMethod,
        bodyMap: LinkedHashMap<String, Any>?,
        className: Class<T>,
        callbackSuccess: (T) -> Unit,
        callbackFailed: (Error) -> Unit
    ) = Thread(Runnable {
        activity?.let {
            if (it is MainActivity) {
                it.showLoading(true)
            } else if (it is LoginActivity) {
                it.showLoading(true)
            }
        }
        val headers = HttpHeaders()
        headers["Cookie"] = session?.idSession
        headers["Content-Type"] = MediaType.APPLICATION_JSON_VALUE
        var error: Error? = null
        var statusCode: HttpStatus
        var responseBody: T? = null
        try {
            val response = restTemplateJsonConverter.exchange(
                "$base_url/$scriptNameWithParams",
                method,
                if (method === HttpMethod.GET) HttpEntity<String>(headers) else HttpEntity(
                    bodyMap,
                    headers
                ),
                className
            )
            statusCode = response.statusCode
            responseBody = response.body
        } catch (ex: HttpClientErrorException) {
            statusCode = ex.statusCode
            error = getErrorByEx(ex)
            activity?.let {
                if (it is MainActivity) {
                    it.errorHandler.showError(error!!)
                    it.showLoading(false)
                } else if (it is LoginActivity) {
                    it.errorHandler.showError(error!!)
                    it.showLoading(false)
                }
            }
        }
        if (statusCode === HttpStatus.OK) {
            callbackSuccess(responseBody!!)
        } else {
            callbackFailed(error!!)
        }
    }).start()

    private fun getError(httpErrorEx: HttpClientErrorException): Error? =
        ObjectMapper().readValue(httpErrorEx.responseBodyAsByteArray, Error::class.java)

    private fun getUnknownError(httpErrorEx: HttpClientErrorException): Error = Error().apply {
        msg = httpErrorEx.message
        code = httpErrorEx.statusCode.value()
    }

    private fun getErrorByEx(httpErrorEx: HttpClientErrorException): Error? =
        httpErrorEx.responseBodyAsByteArray?.let {
            ObjectMapper().readValue(httpErrorEx.responseBodyAsByteArray, Error::class.java)
        }?.also {
            Error().apply {
                msg = httpErrorEx.message
                code = httpErrorEx.statusCode.value()
            }
        }
}