package com.rydvi.clean_vrn.api

import android.app.Activity
import com.rydvi.clean_vrn.MainActivity
import com.rydvi.clean_vrn.ui.login.LoginActivity
import org.codehaus.jackson.map.ObjectMapper
import org.springframework.http.*
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate


object DataRepository {
    private const val base_url = "http://192.168.0.1"

    var selectedGame: Game? = null

    private var session: Session? = null
    fun getSession() = session

    var activity: Activity? = null


    private val restTemplateJsonConverter: RestTemplate = {
        val restTemplate = RestTemplate()
        restTemplate.messageConverters.add(MappingJacksonHttpMessageConverter())
        restTemplate
    }()

    fun getGames(callbackSuccess: (Array<Game>) -> Unit, callbackFailed: (Error) -> Unit) =
        sendRequest(
            "games.php",
            HttpMethod.GET,
            null,
            Array<Game>::class.java,
            {
                callbackSuccess(it!!)
            },
            callbackFailed
        )

    /**
     * Выбор игры. Выбранная игра записывается на сервере в сессию
     */
    fun selectGame(
        id_game: Long,
        callbackSuccess: () -> Unit,
        callbackFailed: (Error) -> Unit
    ) {
        val bodyMap = LinkedHashMap<String, Any>()
        bodyMap["id_game"] = id_game
        sendRequest(
            "select_game.php",
            HttpMethod.POST,
            bodyMap,
            Void::class.java,
            { callbackSuccess() },
            callbackFailed
        )
    }

    fun getTeams(callbackSuccess: (Array<Team>) -> Unit, callbackFailed: (Error) -> Unit) =
        sendRequest(
            "teams.php",
            HttpMethod.GET,
            null,
            Array<Team>::class.java,
            { callbackSuccess(it!!) },
            callbackFailed
        )

    fun getOrganizators(
        callbackSuccess: (Array<Organizator>) -> Unit,
        callbackFailed: (Error) -> Unit
    ) =
        sendRequest(
            "organizators.php",
            HttpMethod.GET,
            null,
            Array<Organizator>::class.java,
            { callbackSuccess(it!!) },
            callbackFailed
        )

    fun getCollectedGarbages(
        id_team: Long,
        callbackSuccess: (Array<CollectedGarbage>) -> Unit,
        callbackFailed: (Error) -> Unit
    ) =
        sendRequest(
            "team_collected_garbages.php?id_team=$id_team",
            HttpMethod.GET,
            null,
            Array<CollectedGarbage>::class.java,
            { callbackSuccess(it!!) },
            callbackFailed
        )

    fun getGarbages(callbackSuccess: (Array<Garbage>) -> Unit?, callbackFailed: (Error) -> Unit) =
        sendRequest(
            "garbages.php",
            HttpMethod.GET,
            null,
            Array<Garbage>::class.java,
            { callbackSuccess(it!!) },
            callbackFailed
        )

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
                callbackSuccess(createdSession!!)
            },
            callbackFailed
        )
    }

    fun logout(callbackSuccess: () -> Unit, callbackFailed: (Error) -> Unit) =
        sendRequest(
            "logout.php",
            HttpMethod.GET,
            null,
            Void::class.java,
            { callbackSuccess() },
            callbackFailed
        )

    fun updateCollectedGarbages(
        id_team: Long,
        collectedGarbage: Array<CollectedGarbage>,
        callbackSuccess: () -> Unit,
        callbackFailed: (Error) -> Unit
    ) {
        val bodyMap = LinkedHashMap<String, Any>()
        bodyMap["collected_garbages"] = collectedGarbage
        sendRequest(
            "team_collected_garbages.php?id_team=$id_team",
            HttpMethod.PUT,
            bodyMap,
            Void::class.java,
            { callbackSuccess() },
            callbackFailed
        )
    }

    fun updateTeam(
        id_team: Long,
        name: String,
        number: Long?,
        callbackSuccess: (team: Team) -> Unit,
        callbackFailed: (Error) -> Unit
    ) {
        val bodyMap = LinkedHashMap<String, Any>()
        bodyMap["name"] = name
        if (number !== null) {
            bodyMap["number"] = number
        }
        sendRequest(
            "teams.php?id_team=$id_team",
            HttpMethod.PUT,
            bodyMap,
            Team::class.java,
            { callbackSuccess(it!!) },
            callbackFailed
        )
    }

    fun createTeam(
        name: String,
        number: Long?,
        callbackSuccess: (team: Team) -> Unit,
        callbackFailed: (Error) -> Unit
    ) {
        val bodyMap = LinkedHashMap<String, Any>()
        bodyMap["name"] = name
        if (number !== null) {
            bodyMap["number"] = number
        }
        sendRequest(
            "teams.php",
            HttpMethod.POST,
            bodyMap,
            Team::class.java,
            { callbackSuccess(it!!) },
            callbackFailed
        )
    }

    fun getCoefficients(
        id_game: Long?,
        callbackSuccess: (Array<Coefficient>) -> Unit,
        callbackFailed: (Error) -> Unit
    ) =
        sendRequest(
            "garbages_coefficients.php?id_game=$id_game",
            HttpMethod.GET,
            null,
            Array<Coefficient>::class.java,
            { callbackSuccess(it!!) },
            callbackFailed
        )

    fun updateGame(
        id: Long,
        name: String,
        route: String,
        datetime: String,
        callbackSuccess: (Game) -> Unit,
        callbackFailed: (Error) -> Unit
    ) {
        val bodyMap = LinkedHashMap<String, Any>()
        bodyMap["name"] = name
        bodyMap["route"] = route
        bodyMap["datetime"] = datetime
        sendRequest(
            "games.php?id_game=$id",
            HttpMethod.PUT,
            bodyMap,
            Game::class.java,
            { callbackSuccess(it!!) },
            callbackFailed
        )
    }

    fun updateCoefficients(
        id: Long,
        coefficients: Array<Coefficient>,
        callbackSuccess: () -> Unit,
        callbackFailed: (Error) -> Unit
    ) {
        val bodyMap = LinkedHashMap<String, Any>()
        bodyMap["coefficients"] = coefficients
        sendRequest(
            "garbages_coefficients.php?id_game=$id",
            HttpMethod.PUT,
            bodyMap,
            Void::class.java,
            { callbackSuccess() },
            callbackFailed
        )
    }

    fun createGame(
        name: String,
        route: String,
        datetime: String,
        callbackSuccess: (Game) -> Unit,
        callbackFailed: (Error) -> Unit
    ) {
        val bodyMap = LinkedHashMap<String, Any>()
        bodyMap["name"] = name
        bodyMap["route"] = route
        bodyMap["datetime"] = datetime
        sendRequest(
            "games.php",
            HttpMethod.POST,
            bodyMap,
            Game::class.java,
            { callbackSuccess(it!!) },
            callbackFailed
        )
    }

    fun createCoefficients(
        id: Long, coefficients: Array<Coefficient>,
        callbackSuccess: (Array<Coefficient>?) -> Unit,
        callbackFailed: (Error) -> Unit
    ) {
        val bodyMap = LinkedHashMap<String, Any>()
        bodyMap["coefficients"] = coefficients
        sendRequest(
            "garbages_coefficients.php?id_game=$id",
            HttpMethod.POST,
            bodyMap,
            Array<Coefficient>::class.java,
            { callbackSuccess(it) },
            callbackFailed
        )
    }

    fun updateOrganizator(
        org: Organizator,
        callbackSuccess: () -> Unit,
        callbackFailed: (Error) -> Unit
    ) {
        val bodyMap = LinkedHashMap<String, Any>()
        bodyMap["lastname"] = org.lastname!!
        bodyMap["firstname"] = org.firstname!!
        bodyMap["middlename"] = org.middlename!!
        bodyMap["email"] = org.email!!
        bodyMap["phone"] = org.phone!!
        sendRequest(
            "organizators.php?id=${org.id}",
            HttpMethod.PUT,
            bodyMap,
            Void::class.java,
            { callbackSuccess() },
            callbackFailed
        )
    }

    fun createOrganizator(
        org: Organizator,
        callbackSuccess: (Organizator) -> Unit,
        callbackFailed: (Error) -> Unit
    ) {
        val bodyMap = LinkedHashMap<String, Any>()
        bodyMap["lastname"] = org.lastname!!
        bodyMap["firstname"] = org.firstname!!
        bodyMap["middlename"] = org.middlename!!
        bodyMap["email"] = org.email!!
        bodyMap["phone"] = org.phone!!
        sendRequest(
            "organizators.php",
            HttpMethod.POST,
            bodyMap,
            Organizator::class.java,
            { callbackSuccess(it!!) },
            callbackFailed
        )
    }

    fun generatePassword(
        id: Long,
        callbackSuccess: (String) -> Unit,
        callbackFailed: (Error) -> Unit
    ) =
        sendRequest(
            "generate_org_password.php?id=$id",
            HttpMethod.GET,
            null,
            Password::class.java,
            { callbackSuccess(it!!.password!!) },
            callbackFailed
        )

    fun deleteGame(id: Long, callbackSuccess: () -> Unit, callbackFailed: (Error) -> Unit) =
        sendRequest(
            "games.php?id_game=$id",
            HttpMethod.DELETE,
            null,
            Void::class.java,
            { callbackSuccess() },
            callbackFailed
        )

    fun deleteOrganizator(id: Long, callbackSuccess: () -> Unit, callbackFailed: (Error) -> Unit) =
        sendRequest(
            "organizators.php?id=$id",
            HttpMethod.DELETE,
            null,
            Void::class.java,
            { callbackSuccess() },
            callbackFailed
        )

    fun deleteTeam(id: Long, callbackSuccess: () -> Unit, callbackFailed: (Error) -> Unit) =
        sendRequest(
            "teams.php?id=$id",
            HttpMethod.DELETE,
            null,
            Void::class.java,
            { callbackSuccess() },
            callbackFailed
        )

    fun completeTheGame(id: Long, callbackSuccess: () -> Unit, callbackFailed: (Error) -> Unit) =
        sendRequest(
            "complete_the_game.php?id=$id",
            HttpMethod.GET,
            null,
            Void::class.java,
            { callbackSuccess() },
            callbackFailed
        )

    fun getPlaces(success: (Array<Place>) -> Unit?, failed: (Error) -> Unit) = sendRequest(
        "games_places.php",
        HttpMethod.GET,
        null,
        Array<Place>::class.java,
        { success(it!!) },
        failed
    )

    fun createPlace(place: Place, success: (Place) -> Unit, failed: (Error) -> Unit) {
        val bodyMap = LinkedHashMap<String, Any>()
        bodyMap["id_place_type"] = place.placeType!!
        bodyMap["description"] = if (place.description !== null) place.description!! else ""
        place.point?.let { point ->
            bodyMap["point"] = LinkedHashMap<String, Double>().apply {
                this["latitude"] = point.latitude!!
                this["longitude"] = point.longitude!!
            }
        }
        place.polygon?.let { polygon ->
            val points = ArrayList<LinkedHashMap<String, Double>>()
            for (point in polygon) {
                points.add(LinkedHashMap<String, Double>().apply {
                    this["latitude"] = point.latitude!!
                    this["longitude"] = point.longitude!!
                })
            }
            bodyMap["polygon"] = points
        }
        sendRequest(
            "games_places.php",
            HttpMethod.POST,
            bodyMap,
            Place::class.java,
            { success(it!!) },
            failed
        )
    }

    fun updatePlace(place: Place, success: () -> Unit, failed: (Error) -> Unit) {
        val bodyMap = LinkedHashMap<String, Any>()
        bodyMap["description"] = if (place.description !== null) place.description!! else ""
        bodyMap["id_place_type"] = place.placeType!!
        place.point?.let { point ->
            bodyMap["point"] = LinkedHashMap<String, Double>().apply {
                this["latitude"] = point.latitude!!
                this["longitude"] = point.longitude!!
            }
        }
        place.polygon?.let { polygon ->
            val points = ArrayList<LinkedHashMap<String, Double>>()
            for (point in polygon) {
                points.add(LinkedHashMap<String, Double>().apply {
                    this["latitude"] = point.latitude!!
                    this["longitude"] = point.longitude!!
                })
            }
            bodyMap["polygon"] = points
        }
        sendRequest(
            "games_places.php?id=${place.id}",
            HttpMethod.PUT,
            bodyMap,
            Void::class.java,
            { success() },
            failed
        )
    }

    fun removePlace(id: Long, success: () -> Unit, failed: (Error) -> Unit) = sendRequest(
        "games_places.php?id=$id",
        HttpMethod.DELETE,
        null,
        Void::class.java,
        { success() },
        failed
    )

    fun <T> sendRequest(
        scriptNameWithParams: String,
        method: HttpMethod,
        bodyMap: LinkedHashMap<String, Any>?,
        className: Class<T>,
        callbackSuccess: (T?) -> Unit,
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
        }
        if (statusCode === HttpStatus.OK || statusCode === HttpStatus.CREATED) {
            activity?.let {
                it.runOnUiThread {
                    callbackSuccess(responseBody)
                    if (it is MainActivity) {
                        it.showLoading(false)
                    } else if (it is LoginActivity) {
                        it.showLoading(false)
                    }
                }
            } ?: run {
                callbackSuccess(responseBody)
            }

        } else {
            callbackFailed(error!!)
            activity?.let {
                if (it is MainActivity) {
                    it.errorHandler.showError(error)
                    it.showLoading(false)
                } else if (it is LoginActivity) {
                    it.errorHandler.showError(error)
                    it.showLoading(false)
                }
            }
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
        } ?: run {
            Error().apply {
                msg = httpErrorEx.message
                code = httpErrorEx.statusCode.value()
            }
        }
}
