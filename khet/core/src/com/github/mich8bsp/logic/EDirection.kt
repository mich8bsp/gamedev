package com.github.mich8bsp.logic

enum class EDirection {
    LEFT, RIGHT, UP, DOWN;

    fun reverse(): EDirection = when(this) {
        DOWN -> UP
        UP -> DOWN
        LEFT -> RIGHT
        RIGHT -> LEFT
    }

    fun rotate(rotationDir: ERotationDirection): EDirection {
        val rotatedDir: EDirection = when(this) {
            DOWN -> LEFT
            UP -> RIGHT
            LEFT -> UP
            RIGHT -> DOWN
        }
        return when (rotationDir) {
            ERotationDirection.CLOCKWISE -> rotatedDir
            ERotationDirection.COUNTER_CLOCKWISE -> rotatedDir.reverse()
        }
    }
}

enum class ERotationDirection {
    CLOCKWISE, COUNTER_CLOCKWISE
}