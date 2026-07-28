package com.github.mich8bsp.logic

class MoveValidator(private val board: Board){
    fun validateMove(move: Move): Boolean{
        return when(move){
            is PositionMove -> validatePositionMove(move)
            is SwitchMove -> validateSwitchMove(move)
            is RotationMove -> validateRotationMove(move)
            else -> false
        }
    }

    private fun validatePositionMove(move: PositionMove): Boolean {
        val validRange = BoardPos.areNeighbors(move.from, move.to)
        val sourceNotSphinx = board.getCell(move.from)?.piece !is SphinxPiece
        val targetNotOccupied = board.getCell(move.to)?.isEmpty() ?: false
        val targetNotOpponentsCell = board.getCell(move.to)?.cellColor != board.getCell(move.from)?.piece?.color?.other()
        return validRange && targetNotOccupied && sourceNotSphinx && targetNotOpponentsCell
    }


    private fun validateSwitchMove(move: SwitchMove): Boolean {
        val validRange = BoardPos.areNeighbors(move.pos1, move.pos2)
        val cell1 = board.getCell(move.pos1)
        val cell2 = board.getCell(move.pos2)

        val switchingTwoPieces = cell1?.piece!=null && cell2?.piece!=null
        val notSwitchingWithSphinx = cell1?.piece !is SphinxPiece && cell2?.piece !is SphinxPiece
        val notOnOpponentsCellAfterSwitch = cell1?.cellColor != cell2?.piece?.color?.other() &&
                cell2?.cellColor != cell1?.piece?.color?.other()

        return validRange && switchingTwoPieces && notSwitchingWithSphinx && notOnOpponentsCellAfterSwitch
    }


    private fun validateRotationMove(move: RotationMove): Boolean {
        val cell = board.getCell(move.pos)
        if(cell?.piece is SphinxPiece){
            return when(cell.piece?.direction){
                EDirection.LEFT, EDirection.RIGHT -> move.direction == ERotationDirection.CLOCKWISE
                EDirection.UP, EDirection.DOWN -> move.direction == ERotationDirection.COUNTER_CLOCKWISE
                else -> false
            }
        }
        return true
    }
}