package com.github.mich8bsp.engine

import com.badlogic.gdx.Gdx

class Engine(implicit world: World) {

  def update() = {
    val dt: Double = Gdx.graphics.getDeltaTime

    val appliedPrevFrameEvents: Seq[Event] = world.entities.flatMap { case (_, entity) =>
      entity.update()
    }.toSeq

    val currentFrameSimulateEvents: Seq[Event] = world.entities.flatMap { case (_, entity) =>
      entity.simulate(dt)
    }.toSeq

    (appliedPrevFrameEvents ++ currentFrameSimulateEvents).foreach {
      case toEvent: ToEntityEvent =>
        world.entities.getOrElse(toEvent.to, throw new Exception(s"Trying to route event to entity which doesn't exist: ${toEvent.to}"))
          .events.enqueue(toEvent)
      case _ =>
    }
  }

  def render() = {
    world.renderWorld()
  }
}
