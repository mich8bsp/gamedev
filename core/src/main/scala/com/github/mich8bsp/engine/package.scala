package com.github.mich8bsp

package object engine {
  type EntityId = Int

  trait Event

  trait ToEntityEvent extends Event {
    def to: EntityId
  }
}
