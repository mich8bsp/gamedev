package com.github.mich8bsp.snake

import com.badlogic.gdx.ApplicationAdapter
import com.github.mich8bsp.engine.Engine

class SnakeGame extends ApplicationAdapter {
  private val inputProcessor = new SnakeInputProcessor
  private var engine: Engine = _

  override def create(): Unit = {
    implicit val world: SnakeGameWorld = new SnakeGameWorld
    engine = new Engine(inputProcessor)
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
