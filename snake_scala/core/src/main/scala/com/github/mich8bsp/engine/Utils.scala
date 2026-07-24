package com.github.mich8bsp.engine

object Utils {

  case class Vec2i(x: Int, y: Int) {
    lazy val size: Int = if (x == 0) {
      math.abs(y)
    } else if (y == 0) {
      math.abs(x)
    } else {
      math.sqrt(x * x + y * y).toInt
    }

    def +(other: Vec2i): Vec2i = Vec2i(x + other.x, y + other.y)
    def unary_- : Vec2i = Vec2i(-x, -y)
    def -(other: Vec2i): Vec2i = this + (-other)
    def *(scal: Int): Vec2i = Vec2i(x * scal, y * scal)
  }

  case class Vec2f(x: Float, y: Float) {
    lazy val size: Float = if (x == 0F) {
      math.abs(y)
    } else if (y == 0F) {
      math.abs(x)
    } else {
      math.sqrt(x * x + y * y).toFloat
    }
    def unary_- : Vec2f = Vec2f(-x, -y)
    def -(other: Vec2f): Vec2f = this + (-other)
    def +(other: Vec2f): Vec2f = Vec2f(x + other.x, y + other.y)
    def *(scal: Float): Vec2f = Vec2f(x * scal, y * scal)
  }

  case class Vec2d(x: Double, y: Double) {
    lazy val size: Double = if (x == 0D) {
      math.abs(y)
    } else if (y == 0D) {
      math.abs(x)
    } else {
      math.sqrt(x * x + y * y)
    }

    def unary_- : Vec2d = Vec2d(-x, -y)
    def -(other: Vec2d): Vec2d = this + (-other)
    def +(other: Vec2d): Vec2d = Vec2d(x + other.x, y + other.y)
    def *(scal: Double): Vec2d = Vec2d(x * scal, y * scal)
  }
}
