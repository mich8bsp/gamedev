package com.github.mich8bsp.snake

import com.badlogic.gdx.{ApplicationAdapter, Gdx}
import com.github.mich8bsp.engine.Utils.Vec2i
import com.github.mich8bsp.engine.{Engine, World}

class SnakeGame extends ApplicationAdapter {
  private val inputProcessor = new SnakeInputProcessor
  private implicit val world: World = new SnakeGameWorld
  private val engine = new Engine(inputProcessor)

  override def create(): Unit = {
    val gridHeight: Int = 32
    val gridWidth: Int = (gridHeight * (Gdx.graphics.getWidth.toDouble / Gdx.graphics.getHeight)).toInt
    val cellSize: Int = (Gdx.graphics.getHeight.toDouble / gridHeight).toInt

    world.entities.put(SnakeEntityIds.SnakeId, new Snake(gridWidth, gridHeight, cellSize))
    world.entities.put(SnakeEntityIds.BerryId, new Berry(gridWidth, gridHeight, cellSize, Vec2i(gridWidth / 2, gridHeight / 4)))
//    world.entities.put(SnakeEntityIds.Score, new ScoreDisplay(Vec2i(gridWidth / 4,  gridHeight / 2), cellSize))
  }

  override def render(): Unit = {
    engine.update()
    engine.render()
  }

  override def dispose(): Unit = {

  }
}
