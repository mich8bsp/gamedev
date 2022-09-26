package com.github.mich8bsp.snake

import com.badlogic.gdx.Gdx
import com.github.mich8bsp.engine.Utils.Vec2i
import com.github.mich8bsp.engine.World

class SnakeGameWorld extends World {
  lazy val gridHeight: Int = 32
  lazy val gridWidth: Int = (gridHeight * (Gdx.graphics.getWidth.toDouble / Gdx.graphics.getHeight)).toInt
  lazy val cellSize: Int = (Gdx.graphics.getHeight.toDouble / gridHeight).toInt

  def getPosOnGrid(x: Int, y: Int): Vec2i = {
    val xOnGrid = (if (x < 0) x + gridWidth else x) % gridWidth
    val yOnGrid = (if (y < 0) y + gridHeight else y) % gridHeight

    Vec2i(xOnGrid, yOnGrid)
  }
}
