package com.github.mich8bsp.logic

interface Piece {
    val color: EPlayerColor
    var health: Int
    var direction: EDirection
    fun isDead(): Boolean = health <= 0
    fun hitWithRay(hitFromDirection: EDirection): EDirection?
    fun rotate(rotationDir: ERotationDirection) {
        direction = direction.rotate(rotationDir)
    }
}

class PharaohPiece(override val color: EPlayerColor, directionFront: EDirection = EDirection.UP) : Piece {
    override var health: Int = 1
    override var direction: EDirection = directionFront

    override fun hitWithRay(hitFromDirection: EDirection): EDirection? {
        health--
        return null
    }
}

class ScarabPiece(override val color: EPlayerColor, directionRightEye: EDirection = EDirection.RIGHT) : Piece {
    override var health: Int = 1
    override var direction: EDirection = directionRightEye

    override fun hitWithRay(hitFromDirection: EDirection): EDirection? {
        val orientationLR: Boolean = when(direction){
            EDirection.UP -> true
            EDirection.RIGHT -> false
            EDirection.DOWN -> true
            EDirection.LEFT -> false
        }
        return Mirror.reflect(hitFromDirection, orientationLR)
    }

}

class PyramidPiece(override val color: EPlayerColor, orientationRightReflection: EDirection) : Piece {
    override var health: Int = 1

    override var direction: EDirection = orientationRightReflection

    override fun hitWithRay(hitFromDirection: EDirection): EDirection? {
        val orientationLR: Boolean = when(direction){
            EDirection.UP -> false
            EDirection.RIGHT -> true
            EDirection.LEFT -> true
            EDirection.DOWN -> false
        }
        val reflected: EDirection = Mirror.reflect(hitFromDirection, orientationLR)
        if(hitFromDirection == direction || reflected == direction){
            return reflected
        }else{
            health--
            return null
        }
    }

}

class AnubisPiece(override val color: EPlayerColor, directionFront: EDirection = EDirection.UP) : Piece {
    override var health: Int = 1
    override var direction: EDirection = directionFront

    override fun hitWithRay(hitFromDirection: EDirection): EDirection? {
        if(hitFromDirection != direction){
            health--
        }
        return null;
    }
}

class SphinxPiece(override val color: EPlayerColor, directionFace: EDirection = EDirection.UP) : Piece {
    override var health: Int = 1
    override var direction: EDirection = directionFace
    override fun hitWithRay(hitFromDirection: EDirection): EDirection? = null
}

class Mirror {
    companion object {
        fun reflect(rayDir: EDirection, orientationLR: Boolean): EDirection {
            val reflected = when (rayDir) {
                EDirection.DOWN -> EDirection.LEFT
                EDirection.UP -> EDirection.RIGHT
                EDirection.LEFT -> EDirection.DOWN
                EDirection.RIGHT -> EDirection.UP
            }
            return if (orientationLR) reflected else reflected.reverse()
        }

    }
}

enum class EPlayerColor{
    GREY, RED;

    fun other(): EPlayerColor =
        when(this){
            GREY -> RED
            RED -> GREY
        }

}