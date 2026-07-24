package com.github.mich8bsp.snake

import com.badlogic.gdx.Gdx
import com.github.mich8bsp.engine.Utils.Vec2i
import com.github.mich8bsp.engine.World

class SnakeGameWorld extends World {
  val gridHeight: Int = 32
  val gridWidth: Int = (gridHeight * (Gdx.graphics.getWidth.toDouble / Gdx.graphics.getHeight)).toInt
  val cellSize: Int = (Gdx.graphics.getHeight.toDouble / gridHeight).toInt

  private val posCache: Array[Vec2i] = {
    val arr: Array[Vec2i] = Array.ofDim(gridWidth * gridHeight)
    for {
      x <- 0 until gridWidth
      y <- 0 until gridHeight
    } yield {
      arr(y * gridWidth + x) = Vec2i(x, y)
    }

    arr
  }

  def getPosOnGrid(x: Int, y: Int): Vec2i = {
    val xOnGrid = (if (x < 0) x + gridWidth else x) % gridWidth
    val yOnGrid = (if (y < 0) y + gridHeight else y) % gridHeight

    posCache(yOnGrid * gridWidth + xOnGrid)
  }
}
