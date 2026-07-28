package com.github.mich8bsp

import scala.collection.mutable

case class GameHistory(){

  private val movesHistory: mutable.Buffer[MoveRecord] = mutable.Buffer[MoveRecord]()

  def addMove(move: Move, playerColor: EPlayerColor): Unit = {
    movesHistory.prepend(MoveRecord(move, playerColor, System.currentTimeMillis()))
  }
  def getLatestMove: Option[MoveRecord] = movesHistory.headOption

  def getPlayerForCurrentTurn: EPlayerColor = getLatestMove.map(_.playerColor.other()).getOrElse(EPlayerColor.GREY)
}

case class MoveRecord(move: Move, playerColor: EPlayerColor, recordId: Long) {
  def adaptForPlayer(playerToAdaptTo: EPlayerColor): MoveRecord = {
    if(playerToAdaptTo == playerColor){
      this
    }else{
      this.copy(move = move.reverse())
    }
  }

}