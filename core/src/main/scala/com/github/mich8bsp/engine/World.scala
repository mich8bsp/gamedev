package com.github.mich8bsp.engine

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer

import scala.collection.mutable

trait World {
  lazy val batch = new SpriteBatch()
  implicit lazy val shapeRenderer = new ShapeRenderer()

  private [engine] val entities = mutable.Map[EntityId, Entity[_]]()

  def setEntity(id: EntityId, entity: Entity[_]): Unit = {
    if (entities.contains(id)) {
      throw new Exception(s"Could not add entity with id $id: another entity with that id already exists")
    }
    entities.put(id, entity)
  }

  def getEntity[T <: Entity[_]](id: EntityId): Option[T] = entities.get(id).flatMap {
    case x: T => Some(x)
    case _ => None
  }

  def renderWorld(): Unit = {
    Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
    batch.begin()
    entities
      .filter(_._2.isVisible)
      .foreach(_._2.render(batch))
    batch.end()
  }
}

trait Entity[WorldImpl <: World] {
  val world: WorldImpl

  var isActive: Boolean = true
  var isVisible: Boolean = true
  val events: mutable.Queue[ToEntityEvent] = mutable.Queue.empty[ToEntityEvent]

  def render(batch: SpriteBatch)(implicit shapeRenderer: ShapeRenderer): Unit
  def update(): Seq[Event]
  def simulate(dt: Double): Seq[Event]
}
