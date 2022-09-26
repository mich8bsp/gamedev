package com.github.mich8bsp.engine

trait Event

trait ToEntityEvent extends Event {
  def to: EntityId
}