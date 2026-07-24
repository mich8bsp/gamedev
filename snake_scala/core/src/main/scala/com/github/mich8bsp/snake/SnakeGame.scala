package com.github.mich8bsp.snake

import com.badlogic.gdx.ApplicationAdapter
import com.github.mich8bsp.engine.Engine

class SnakeGame extends ApplicationAdapter {
  private var engine: Engine = _

  override def create(): Unit = {
    implicit val world: SnakeGameWorld = new SnakeGameWorld
    engine = new Engine()
    world.setEntity(SnakeEntityIds.SnakeId, new Snake())
    world.setEntity(SnakeEntityIds.BerryId, new Berry())
    world.setEntity(SnakeEntityIds.Score, new ScoreDisplay(world.getPosOnGrid(3, 3)))
    world.setEntity(SnakeEntityIds.SnakeController, new SnakeController())
  }

  override def render(): Unit = {
    engine.update()
    engine.render()
  }

  override def dispose(): Unit = {

  }
}
