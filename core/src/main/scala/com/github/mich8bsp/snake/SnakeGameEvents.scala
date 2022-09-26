package com.github.mich8bsp.snake

import com.github.mich8bsp.engine.Utils.Vec2i
import com.github.mich8bsp.engine.{EntityId, ToEntityEvent}

object SnakeGameEvents {

  case class TurnLeftRequested(to: EntityId) extends ToEntityEvent
  case class TurnRightRequested(to: EntityId) extends ToEntityEvent
  case class TurnUpRequested(to: EntityId) extends ToEntityEvent
  case class TurnDownRequested(to: EntityId) extends ToEntityEvent

  case class TurnLeft(to: EntityId) extends ToEntityEvent
  case class TurnRight(to: EntityId) extends ToEntityEvent
  case class TurnUp(to: EntityId) extends ToEntityEvent
  case class TurnDown(to: EntityId) extends ToEntityEvent

  case class MoveEntity(to: EntityId, dpos: Vec2i) extends ToEntityEvent

  case class BerryEaten(to: EntityId) extends ToEntityEvent
}
