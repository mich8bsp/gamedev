package com.github.mich8bsp

trait Move {
  def reverse(): Move
  def moveType: EMoveType
}

case class RotationMove( pos: BoardPos,  direction: ERotationDirection) extends Move {
  override def reverse(): Move = RotationMove(pos.reverse(), direction)
  override def moveType: EMoveType = EMoveType.ROTATE
}
case class PositionMove( from: BoardPos,  to: BoardPos) extends Move {
  override def reverse(): Move = PositionMove(from.reverse(), to.reverse())
  override def moveType: EMoveType = EMoveType.POSITION
}
case class SwitchMove( pos1: BoardPos,  pos2: BoardPos) extends Move {
  override def reverse(): Move = SwitchMove(pos1.reverse(), pos2.reverse())
  override def moveType: EMoveType = EMoveType.SWITCH
}

case class BoardPos(i: Int, j: Int){
  def reverse(): BoardPos = BoardPos(GameConfig.ROWS-i-1, GameConfig.COLS-j-1)
}