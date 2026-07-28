package com.github.mich8bsp

import java.util.UUID

import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import com.twitter.finatra.http.annotations.QueryParam

class GameController extends Controller{
  import GameManager.ec

  println("game controller created")

  get("/server/status"){ request: Request =>
    "ok"
  }

  get("/game/is_room_ready"){
    request: RequestByPlayerId =>
      GameManager.isGameRoomReady(UUID.fromString(request.playerId))
  }

  post("/game/join"){
    request: Request => GameManager.joinGame
  }

  post("/game/make_pos_move"){
    request: MakePositionMoveRequest =>
      GameManager.makeMove(request.move, UUID.fromString(request.playerId))
  }

  post("/game/make_rot_move"){
    request: MakeRotationMoveRequest =>
      GameManager.makeMove(request.move, UUID.fromString(request.playerId))
  }

  post("/game/make_switch_move"){
    request: MakeSwitchMoveRequest=>
      GameManager.makeMove(request.move, UUID.fromString(request.playerId))
  }

  get("/game/get_latest_move"){
    request: RequestByPlayerId => {
      GameManager.getLatestMove(UUID.fromString(request.playerId))
        .map(move => GetMoveResponse(move, move.map(_.move.moveType).getOrElse(EMoveType.NONE)))
    }
  }


  get("/game/clear"){
    request: Request => GameManager.resetAllGames
  }

}

case class MakePositionMoveRequest(
                                  move: PositionMove,
                                  playerId: String
                                  )

case class MakeRotationMoveRequest(
                                  move: RotationMove,
                                  playerId: String
                                  )
case class MakeSwitchMoveRequest(
                                move: SwitchMove,
                                playerId: String
                                )

case class RequestByPlayerId(
                         @QueryParam playerId: String
                         )

case class GetMoveResponse(
                          move: Option[MoveRecord],
                          moveType: EMoveType
                          )