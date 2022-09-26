package com.github.mich8bsp.engine

trait InputProcessor {
  def process(): Seq[Event]
}
