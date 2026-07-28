package com.github.mich8bsp.logic

interface Move{
    fun getMoveType(): EMoveType
}

data class RotationMove(val pos: BoardPos, val direction: ERotationDirection) : Move {
    override fun getMoveType(): EMoveType = EMoveType.ROTATE
}

data class PositionMove(val from: BoardPos, val to: BoardPos): Move {
    override fun getMoveType(): EMoveType = EMoveType.POSITION
}
data class SwitchMove(val pos1: BoardPos, val pos2: BoardPos): Move {
    override fun getMoveType(): EMoveType = EMoveType.SWITCH
}

data class MoveRecord<T : Move>(val move: T, val playerColor: EPlayerColor, val recordId: Long)