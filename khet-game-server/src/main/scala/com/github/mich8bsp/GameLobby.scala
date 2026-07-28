package com.github.mich8bsp

import java.util.UUID

import akka.actor.Actor

import scala.collection.mutable

class GameLobbyActor extends Actor{
  private val gameRooms: mutable.Map[UUID, GameRoom] = mutable.Map[UUID, GameRoom]()
  private val playersById: mutable.Map[UUID, Player] = mutable.Map[UUID, Player]()

  override def receive: Receive = {
    case JoinGameRequest =>
      val roomNotFull: Option[GameRoom] = gameRooms.values.find(_.roomState == EGameRoomState.AWAITING_PLAYERS)
      val roomToJoin: GameRoom = roomNotFull.getOrElse(createRoom)
      val newPlayer: Player = roomToJoin.joinAsPlayer
      playersById.put(newPlayer.playerId, newPlayer)
      sender().tell(newPlayer, self)
    case ClearGamesRequest =>
      gameRooms.clear()
      playersById.clear()
      sender().tell(true, self)
    case MakeMove(move, playerId) => {
      val player: Option[Player] = playersById.get(playerId)
      val gameRoom: Option[GameRoom] = player.flatMap(p => gameRooms.get(p.roomId))
      val makingMoveRes: Boolean = gameRoom.exists(room => {
        room.addMove(move, player.get.color)
      })
      sender.tell(makingMoveRes, self)
    }
    case GetMove(playerId) => {
      val player: Option[Player] = playersById.get(playerId)
      val gameRoom: Option[GameRoom] = getRoomByPlayerId(playerId)
      val move = gameRoom.flatMap(_.getLatestMove)
      val moveAdapted = move.map(_.adaptForPlayer(player.get.color))

      sender.tell(moveAdapted, self)
    }
    case IsGameRoomReady(playerId) => {
      val gameRoom: Option[GameRoom] = getRoomByPlayerId(playerId)
      val isReady: Boolean = gameRoom.exists(_.roomState == EGameRoomState.GAME_ONGOING)

      sender.tell(isReady, self)
    }
  }

  private def getRoomByPlayerId(playerId: UUID): Option[GameRoom] = {
    val player: Option[Player] = playersById.get(playerId)
    player.flatMap(p => gameRooms.get(p.roomId))
  }

  private def createRoom: GameRoom = {
    val newRoom = GameRoom()
    gameRooms.put(newRoom.id, newRoom)
    newRoom
  }
}

case class GameRoom(
                   id: UUID = UUID.randomUUID()
                   ){
  var player1: Option[Player] = None
  var player2: Option[Player] = None
  val gameHistory: GameHistory = GameHistory()

  def roomState: EGameRoomState = {
    if(player1.isDefined && player2.isDefined){
      EGameRoomState.GAME_ONGOING
    }else{
      EGameRoomState.AWAITING_PLAYERS
    }
  }

  def joinAsPlayer: Player = (player1, player2) match {
    case (Some(_), Some(_)) => throw new Exception("Can't join a full room")
    case (Some(p1), None) =>
      player2 = Some(Player.create(id, p1.color.other()))
      player2.get
    case (None, Some(p2)) =>
      player1 = Some(Player.create(id, p2.color.other()))
      player1.get
    case (None, None) =>
      player1 = Some(Player.create(id, EPlayerColor.GREY))
      player1.get
  }

  def addMove(move: Move, playerColor: EPlayerColor): Boolean = {
    gameHistory.addMove(move, playerColor)
    true
  }

  def getLatestMove: Option[MoveRecord] = gameHistory.getLatestMove
}
