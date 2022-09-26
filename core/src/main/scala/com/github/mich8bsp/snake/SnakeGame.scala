package com.github.mich8bsp.snake

import com.badlogic.gdx.ApplicationAdapter
import com.github.mich8bsp.engine.Engine

class SnakeGame extends ApplicationAdapter {
  private val inputProcessor = new SnakeInputProcessor
  private implicit val world: SnakeGameWorld = new SnakeGameWorld
  private val engine = new Engine(inputProcessor)

  override def create(): Unit = {
    world.setEntity(SnakeEntityIds.SnakeId, new Snake())
    world.setEntity(SnakeEntityIds.BerryId, new Berry())
  }

  override def render(): Unit = {
    engine.update()
    engine.render()
  }

  override def dispose(): Unit = {

  }
}
