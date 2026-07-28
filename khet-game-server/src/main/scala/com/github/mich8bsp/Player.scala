package com.github.mich8bsp

import java.util.UUID

trait PlayerId{
  val playerId: UUID
}

case class Player(playerId: UUID, roomId: UUID, color: EPlayerColor) extends PlayerId

object Player {
  def create(roomId: UUID, color: EPlayerColor): Player = Player(
    playerId = UUID.randomUUID(),
    roomId = roomId,
    color = color)
}
