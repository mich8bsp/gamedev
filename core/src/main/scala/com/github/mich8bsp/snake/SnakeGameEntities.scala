package com.github.mich8bsp.snake

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.badlogic.gdx.math.MathUtils
import com.github.mich8bsp.engine.Utils._
import com.github.mich8bsp.engine.{Entity, Event, Renderer}
import com.github.mich8bsp.snake.SnakeGameEvents._

import scala.collection.mutable

object SnakeEntityIds {
  val SnakeId = 1
  val BerryId = 2
  val Score = 3
}

class ScoreDisplay(pos: Vec2i)(implicit val world: SnakeGameWorld) extends Entity[SnakeGameWorld] {
  private var score: Int = 0
  private lazy val bmfont: BitmapFont = new BitmapFont()
  private var isGameOver: Boolean = false
  private var timeSinceBerryEaten: Double = 0D
  private val ScoreFadeTimeSec: Double = 3D

  override def render(implicit renderer: Renderer): Unit = {
    val textPosX: Float = world.cellSize * pos.x
    val textPosY: Float = world.cellSize * pos.y
    if (isGameOver) {
      renderer.batch.begin()
      bmfont.setColor(Color.WHITE)
      bmfont.draw(renderer.batch,  s"Game Over! Score: ${score}", textPosX, textPosY)
      renderer.batch.end()
    } else {
      val alpha: Float = (math.max(ScoreFadeTimeSec - timeSinceBerryEaten, 0D) / ScoreFadeTimeSec).toFloat
      if (alpha > 0) {
        renderer.batch.begin()
        bmfont.setColor(Color.WHITE.set(1f,1f,1f,alpha))
        bmfont.draw(renderer.batch,s"$score", textPosX, textPosY)
        renderer.batch.end()
      }
    }

  }

  override def update(): Seq[Event] = {
    while (events.nonEmpty) {
      events.dequeue() match {
        case _: BerryEaten =>
          timeSinceBerryEaten = 0D
          score += 1
        case _: GameOver =>
          isGameOver = true
      }
    }
    Seq.empty
  }

  override def simulate(dt: Double): Seq[Event] = {
    timeSinceBerryEaten += dt
    Seq.empty
  }
}

class Berry(implicit val world: SnakeGameWorld) extends Entity[SnakeGameWorld] {
  var position: Option[Vec2i] = None

  private def doesIntersectWithSnake(pos: Vec2i): Boolean = {
    world.getEntity[Snake](SnakeEntityIds.SnakeId).exists {
      _.positions.contains(pos)
    }
  }

  private def generatePosition: Vec2i = {
    var generatedPosition = world.getPosOnGrid(MathUtils.random(0, world.gridWidth), MathUtils.random(0, world.gridHeight))
    while (position.contains(generatedPosition) || doesIntersectWithSnake(generatedPosition)) {
      generatedPosition = world.getPosOnGrid(MathUtils.random(0, world.gridWidth), MathUtils.random(0, world.gridHeight))
    }
    generatedPosition
  }

  override def render(implicit renderer: Renderer): Unit = {
    position.foreach {
      case Vec2i(gridX, gridY) =>
        renderer.shapeRenderer.begin(ShapeType.Filled)
        renderer.shapeRenderer.setColor(Color.MAROON)
        val x = world.cellSize * gridX
        val y = world.cellSize * gridY
        renderer.shapeRenderer.rect(x, y, world.cellSize, world.cellSize)
        renderer.shapeRenderer.end()
    }
  }

  override def update(): Seq[Event] = {
    if (position.isEmpty) {
      position = Some(generatePosition)
    }
    while (events.nonEmpty) {
      events.dequeue() match {
        case _: BerryEaten =>
          position = Some(generatePosition)
      }
    }
    Seq.empty
  }

  override def simulate(dt: Double): Seq[Event] = Seq.empty
}

class Snake(implicit val world: SnakeGameWorld) extends Entity[SnakeGameWorld] {
  private var vel: Vec2d = Vec2d(2.5D, 0D)
  private var dtSinceMovement: Double = 0D
  private val initialSize: Int = 5
  val positions: mutable.Buffer[Vec2i] = (-initialSize / 2 to initialSize / 2)
    .map(x => world.getPosOnGrid(world.gridWidth / 2 + x, world.gridHeight / 2))
    .toBuffer

  override def render(implicit renderer: Renderer): Unit = {
    renderer.shapeRenderer.begin(ShapeType.Line)
    renderer.shapeRenderer.setColor(Color.ROYAL)
    positions.foreach {
      pos => {
        val x = world.cellSize * pos.x
        val y = world.cellSize * pos.y
        renderer.shapeRenderer.rect(x, y, world.cellSize, world.cellSize)
      }
    }
    renderer.shapeRenderer.end()
  }

  override def update(): Seq[Event] = {
    val outputEvents = mutable.Buffer.empty[Event]
    while (events.nonEmpty) {
      events.dequeue() match {
        case MoveEntity(_, dpos) =>
          val snakeHead = positions.last
          val newSnakeHead = snakeHead + dpos

          if (positions.contains(newSnakeHead)) {
            vel = Vec2d(0D, 0D)
            outputEvents.append(GameOver(SnakeEntityIds.Score))
          } else {
            val berryPosition: Option[Vec2i] = world.getEntity[Berry](SnakeEntityIds.BerryId)
              .flatMap(_.position)

            val snakeIncreased: Int = if (berryPosition.contains(newSnakeHead)) {
              outputEvents.append(BerryEaten(SnakeEntityIds.BerryId))
              outputEvents.append(BerryEaten(SnakeEntityIds.Score))
              vel = vel * 1.2
              1
            } else {
              0
            }

            val newSnakePositions = if (snakeHead.x < newSnakeHead.x) {
              (snakeHead.x to newSnakeHead.x).tail.map(x => world.getPosOnGrid(x, snakeHead.y))
            } else if (snakeHead.x > newSnakeHead.x) {
              (newSnakeHead.x to snakeHead.x).reverse.tail.map(x => world.getPosOnGrid(x, snakeHead.y))
            } else if (snakeHead.y < newSnakeHead.y) {
              (snakeHead.y to newSnakeHead.y).tail.map(y => world.getPosOnGrid(snakeHead.x, y))
            } else {
              (newSnakeHead.y to snakeHead.y).reverse.tail.map(y => world.getPosOnGrid(snakeHead.x, y))
            }

            positions.appendAll(newSnakePositions)
            positions.dropInPlace(newSnakePositions.size - snakeIncreased)
          }
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

  override def simulate(dt: Double): Seq[Event] = {
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
}
