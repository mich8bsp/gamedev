package com.github.mich8bsp.snake

import com.badlogic.gdx.{Gdx, Input}
import com.github.mich8bsp.engine.{Event, InputProcessor}
import com.github.mich8bsp.snake.SnakeGameEvents.{TurnDownRequested, TurnLeftRequested, TurnRightRequested, TurnUpRequested}

class SnakeInputProcessor extends InputProcessor {
  override def process(): Seq[Event] = {
    if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
      Seq(TurnLeftRequested(SnakeEntityIds.SnakeId))
    } else if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
      Seq(TurnRightRequested(SnakeEntityIds.SnakeId))
    } else if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
      Seq(TurnUpRequested(SnakeEntityIds.SnakeId))
    } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
      Seq(TurnDownRequested(SnakeEntityIds.SnakeId))
    } else {
      Seq.empty
    }
  }
}
