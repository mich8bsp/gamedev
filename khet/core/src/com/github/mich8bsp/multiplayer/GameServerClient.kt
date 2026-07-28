package com.github.mich8bsp.multiplayer

import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.mich8bsp.logic.*
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.utils.EmptyContent
import io.ktor.content.TextContent
import io.ktor.http.ContentType
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.util.*

object GameServerClient {
    private val client = HttpClient()
    val isLocal = false
    private val scheme = if(isLocal) "http" else "https"
    private val serverURL = if(isLocal) "localhost" else "floating-temple-17106.herokuapp.com"
    private val port = if(isLocal) 9992 else io.ktor.http.DEFAULT_PORT
    private val objectMapper: ObjectMapper = createObjMapper()

    private fun createObjMapper(): ObjectMapper {
        val mapper = jacksonObjectMapper()
                .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
        return mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    fun joinGameAsync(): Deferred<Player> {
        return GlobalScope.async {
            objectMapper.readValue<Player>(postRequest("/game/join"))
        }
    }

    fun sendMove(move: Move, playerId: UUID) {
        GlobalScope.async {
            val body = objectMapper.writeValueAsString(MoveRequest(move, playerId.toString()))
            val path = when(move){
                is RotationMove -> "make_rot_move"
                is PositionMove -> "make_pos_move"
                is SwitchMove -> "make_switch_move"
                else -> throw Exception("unrecognized move type $move")
            }
            postRequest("/game/$path", TextContent(body, contentType = ContentType.Application.Json))
        }
    }

    fun getLatestMove(playerId: UUID): Deferred<MoveRecord<out Move>?> {
        return GlobalScope.async {
            val response = getRequest("/game/get_latest_move?player_id=$playerId")
            val responseAsTypedMove = objectMapper.readValue<MoveTypeResponse>(response)
            when(responseAsTypedMove.moveType){
                EMoveType.NONE -> null
                EMoveType.POSITION -> objectMapper.readValue<MoveFullResponse<PositionMove>>(response)
                        .move
                EMoveType.ROTATE -> objectMapper.readValue<MoveFullResponse<RotationMove>>(response)
                        .move
                EMoveType.SWITCH -> objectMapper.readValue<MoveFullResponse<SwitchMove>>(response)
                        .move
            }
        }
    }

    fun isGameRoomReady(playerId: UUID): Deferred<Boolean> {
        return GlobalScope.async {
            val response = getRequest("/game/is_room_ready?player_id=$playerId")
            val isReady = objectMapper.readValue<Boolean>(response)
            isReady
        }
    }

    private suspend fun postRequest(url: String, body: Any = EmptyContent): String {
        println("sending POST request $body to $url")
        return client.post<String>(scheme = scheme, host = serverURL, port = port, path = url, body = body)
    }

    private suspend fun getRequest(url: String): String {
        println("sending GET request to $url")
        return client.get<String>(scheme = scheme, host = serverURL, port = port, path = url)
    }
}

data class MoveRequest(val move: Move, val playerId: String)
data class MoveTypeResponse(val moveType: EMoveType)
data class MoveFullResponse<T: Move>(val move: MoveRecord<T>, val moveType: EMoveType)