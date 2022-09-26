package com.github.mich8bsp.engine

trait Event

trait FromEntityEvent extends Event {
  def from: EntityId
}

trait ToEntityEvent extends Event {
  def to: EntityId
}