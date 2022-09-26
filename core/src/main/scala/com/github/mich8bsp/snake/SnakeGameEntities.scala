package com.github.mich8bsp.snake

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.{BitmapFont, SpriteBatch}
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.badlogic.gdx.math.MathUtils
import com.github.mich8bsp.engine.Utils._
import com.github.mich8bsp.engine.{Entity, Event, World}
import com.github.mich8bsp.snake.SnakeGameEvents._

import scala.collection.mutable

object SnakeEntityIds {
  val SnakeId = 1
  val BerryId = 2
  val Score = 3
}

class ScoreDisplay(pos: Vec2i, cellSize: Int) extends Entity {
  var score: Int = 0
  val bmfont = new BitmapFont()
  bmfont.setColor(Color.WHITE)

  override def render(batch: SpriteBatch)(implicit shapeRenderer: ShapeRenderer): Unit = {
    bmfont.draw(batch, score.toString, cellSize * pos.x, cellSize * pos.y)
  }

  override def update(implicit world: World): Seq[Event] = {
    while (events.nonEmpty) {
      events.dequeue() match {
        case _: BerryEaten => score += 1
      }
    }
    Seq.empty
  }

  override def simulate(dt: Double)(implicit world: World): Seq[Event] = Seq.empty
}

class Berry(gridWidth: Int, gridHeight: Int, cellSize: Float, var position: Vec2i) extends Entity {

  override def render(batch: SpriteBatch)(implicit shapeRenderer: ShapeRenderer): Unit = {
    shapeRenderer.begin(ShapeType.Filled)
    shapeRenderer.setColor(Color.MAROON)
    val x = cellSize * position.x
    val y = cellSize * position.y
    shapeRenderer.rect(x, y, cellSize, cellSize)
    shapeRenderer.end()
  }

  override def update(implicit world: World): Seq[Event] = {
    while (events.nonEmpty) {
      events.dequeue() match {
        case _: BerryEaten =>
          val currentPosition = position
          while (position == currentPosition ||
              world.entities(SnakeEntityIds.SnakeId).asInstanceOf[Snake].positions.contains(position)) {
              position = Vec2i(MathUtils.random(0, gridWidth), MathUtils.random(0, gridHeight))
          }
      }
    }
    Seq.empty
  }

  override def simulate(dt: Double)(implicit world: World): Seq[Event] = Seq.empty
}

class Snake(gridWidth: Int, gridHeight: Int, cellSize: Int) extends Entity {
  private var vel: Vec2d = Vec2d(1.5D, 0D)
  private var dtSinceMovement: Double = 0D
  private val initialSize: Int = 5
  val positions: mutable.Buffer[Vec2i] = (-initialSize / 2 to initialSize / 2)
    .map(x => getPosOnGrid(gridWidth / 2 + x, gridHeight / 2))
    .toBuffer

  override def render(batch: SpriteBatch)
                     (implicit shapeRenderer: ShapeRenderer): Unit = {
    shapeRenderer.begin(ShapeType.Line)
    shapeRenderer.setColor(Color.ROYAL)
    positions.foreach {
      pos => {
        val x = cellSize * pos.x
        val y = cellSize * pos.y
        shapeRenderer.rect(x, y, cellSize, cellSize)
      }
    }
    shapeRenderer.end()
  }

  override def update(implicit world: World): Seq[Event] = {
    val outputEvents = mutable.Buffer.empty[Event]
    while (events.nonEmpty) {
      events.dequeue() match {
        case MoveEntity(_, dpos) =>
          val snakeHead = positions.last
          val newSnakeHead = snakeHead + dpos

          if (positions.contains(newSnakeHead)) {
            println("Game Over")
            System.exit(0)
          }

          val berryPosition: Option[Vec2i] = world.entities.get(SnakeEntityIds.BerryId)
            .flatMap {
              case berry: Berry => Some(berry.position)
              case _ => None
            }

          val snakeIncreased: Int = if (berryPosition.contains(newSnakeHead)) {
            outputEvents.append(BerryEaten(SnakeEntityIds.BerryId))
            vel = vel * 1.2
            1
          } else {
            0
          }

          val newSnakePositions = if (snakeHead.x < newSnakeHead.x) {
            (snakeHead.x to newSnakeHead.x).tail.map(x => getPosOnGrid(x, snakeHead.y))
          } else if (snakeHead.x > newSnakeHead.x) {
            (newSnakeHead.x to snakeHead.x).reverse.tail.map(x => getPosOnGrid(x, snakeHead.y))
          } else if (snakeHead.y < newSnakeHead.y) {
            (snakeHead.y to newSnakeHead.y).tail.map(y => getPosOnGrid(snakeHead.x, y))
          } else {
            (newSnakeHead.y to snakeHead.y).reverse.tail.map(y => getPosOnGrid(snakeHead.x, y))
          }

          positions.appendAll(newSnakePositions)
          positions.dropInPlace(newSnakePositions.size - snakeIncreased)
        case TurnUpRequested(to) if vel.y == 0D =>
          outputEvents.append(TurnUp(to))
        case TurnUpRequested(_) =>
        case TurnDownRequested(to) if vel.y == 0D =>
          outputEvents.append(TurnDown(to))
        case TurnDownRequested(_) =>
        case TurnLeftRequested(to) if vel.x == 0D =>
          outputEvents.append(TurnLeft(to))
        case TurnLeftRequested(_) =>
        case TurnRightRequested(to) if vel.x == 0D =>
          outputEvents.append(TurnRight(to))
        case TurnRightRequested(_) =>
        case TurnUp(_) =>
          vel = Vec2d(0D, math.abs(vel.x))
        case TurnDown(_) =>
          vel = Vec2d(0D, -math.abs(vel.x))
        case TurnRight(_) =>
          vel = Vec2d(math.abs(vel.y), 0D)
        case TurnLeft(_) =>
          vel = Vec2d(-math.abs(vel.y), 0D)
      }
    }

    outputEvents.toSeq
  }

  override def simulate(dt: Double)(implicit world: World): Seq[Event] = {
    dtSinceMovement += dt
    val dpos = vel * dtSinceMovement
    if (dpos.size > 1D) {
      val dposCells = Vec2i(dpos.x.toInt, dpos.y.toInt)
      dtSinceMovement = (dpos - Vec2d(dposCells.x.toDouble, dposCells.y.toDouble)).size / vel.size
      Seq(MoveEntity(SnakeEntityIds.SnakeId, dposCells))
    } else {
      Seq.empty
    }
  }

  private def getPosOnGrid(x: Int, y: Int): Vec2i = {
    val xOnGrid = (if (x < 0) x + gridWidth else x) % gridWidth
    val yOnGrid = (if (y < 0) y + gridHeight else y) % gridHeight

    Vec2i(xOnGrid, yOnGrid)
  }
}
