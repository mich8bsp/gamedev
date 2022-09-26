package com.github.mich8bsp.engine

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer

class Renderer {
  lazy val batch = new SpriteBatch()
  lazy val shapeRenderer = new ShapeRenderer()
}
