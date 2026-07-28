package com.github.mich8bsp

import java.util.UUID
import java.util.concurrent.{Executors, TimeUnit}

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Future}

object GameManager  {

  implicit val ec: ExecutionContext = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(15))
  private val actorSystem = ActorSystem("khet-game-server")
  private val gameManagerActor = actorSystem.actorOf(Props(new GameManagerActor))
  private implicit val timeout: Timeout = Timeout(Duration(1, TimeUnit.MINUTES))

  def joinGame: Future[Player] = {
    gameManagerActor.ask(JoinGameRequest)
      .mapTo[Player]
  }

  def resetAllGames: Future[Boolean] = {
    gameManagerActor.ask(ClearGamesRequest)
      .mapTo[Boolean]
  }

  def makeMove(move: Move, playerId: UUID): Future[Boolean] = {
    gameManagerActor.tell(MakeMove(move, playerId), ActorRef.noSender)
    Future.successful(true)
  }

  def getLatestMove(playerId: UUID): Future[Option[MoveRecord]] = {
    gameManagerActor.ask(GetMove(playerId))
      .mapTo[Option[MoveRecord]]
  }
  def isGameRoomReady(playerId: UUID): Future[Boolean] = {
    gameManagerActor.ask(IsGameRoomReady(playerId))
      .mapTo[Boolean]
  }
}

class GameManagerActor extends Actor {
  val gameLobby: ActorRef = context.actorOf(Props(new GameLobbyActor))
  override def receive: Receive = {
    case JoinGameRequest => gameLobby.tell(JoinGameRequest, sender)
    case ClearGamesRequest => gameLobby.tell(ClearGamesRequest, sender)
    case makeMove: MakeMove => gameLobby.tell(makeMove, sender)
    case getMove: GetMove => gameLobby.tell(getMove, sender)
    case isGameRoomReady: IsGameRoomReady => gameLobby.tell(isGameRoomReady, sender)
  }
}

case object JoinGameRequest
case object ClearGamesRequest
case class MakeMove(move: Move, playerId: UUID)
case class GetMove(playerId: UUID)
case class IsGameRoomReady(playerId: UUID)