package com.github.mich8bsp.logic

import com.github.mich8bsp.logic.BoardConfigurationFactor.Companion.buildClassic

enum class EBoardConfigurations(val configuration: Map<BoardPos, Piece>, val rows: Int, val cols: Int) {
    CLASSIC_GREY(buildClassic(EPlayerColor.GREY, 8, 10), 8, 10),
    CLASSIC_RED(buildClassic(EPlayerColor.RED, 8, 10), 8, 10),
    DYNASTY_GREY(mapOf(), 8, 10),
    DYNASTY_RED(mapOf(), 8, 10),
    IMHOTEP_GREY(mapOf(), 8, 10),
    IMHOTEP_RED(mapOf(), 8, 10);

    fun getBoardSize(): Pair<Int, Int> = Pair<Int, Int>(rows, cols)

}

class BoardConfigurationFactor {
    companion object {

        fun buildClassic(playerColor: EPlayerColor, rows: Int, cols: Int): Map<BoardPos, Piece> {
            val playerSide =  buildClassicSide(rows, cols, playerColor)
            val opponentSide = asOppositeSide(rows, cols, playerSide)
            return playerSide + opponentSide
        }

        private fun buildClassicSide(rows: Int, cols: Int, playerColor: EPlayerColor): Map<BoardPos, Piece> {
            val opponentColor: EPlayerColor = playerColor.other()
            return mapOf(
                    BoardPos.get(0, 2) to PyramidPiece(playerColor, EDirection.UP),
                    BoardPos.get(0, 3) to AnubisPiece(playerColor),
                    BoardPos.get(0, 4) to PharaohPiece(playerColor),
                    BoardPos.get(0, 5) to AnubisPiece(playerColor),
                    BoardPos.get(0, 9) to SphinxPiece(playerColor),
                    BoardPos.get(1, 7) to PyramidPiece(playerColor, EDirection.RIGHT),
                    BoardPos.get(2, 6) to PyramidPiece(opponentColor, EDirection.DOWN),
                    BoardPos.get(3, 0) to PyramidPiece(opponentColor, EDirection.DOWN),
                    BoardPos.get(3, 2) to PyramidPiece(playerColor, EDirection.UP),
                    BoardPos.get(3, 4) to ScarabPiece(playerColor, EDirection.LEFT),
                    BoardPos.get(3, 5) to ScarabPiece(playerColor, EDirection.DOWN),
                    BoardPos.get(3, 7) to PyramidPiece(opponentColor, EDirection.RIGHT),
                    BoardPos.get(3, 9) to PyramidPiece(playerColor, EDirection.LEFT)
            )
        }

        private fun asOppositeSide(rows: Int, cols: Int, mapOfPieces: Map<BoardPos, Piece>): Map<BoardPos, Piece> {
            return mapOfPieces.map { entry ->
                asOppositeSidePos(entry.key, rows, cols) to asOppositeSidePiece(entry.value)
            }.toMap()
        }

        private fun asOppositeSidePiece(piece: Piece): Piece {
            return when(piece){
                is SphinxPiece -> SphinxPiece(piece.color.other(), piece.direction.reverse())
                is PyramidPiece -> PyramidPiece(piece.color.other(), piece.direction.reverse())
                is AnubisPiece -> AnubisPiece(piece.color.other(), piece.direction.reverse())
                is ScarabPiece -> ScarabPiece(piece.color.other(), piece.direction.reverse())
                is PharaohPiece -> PharaohPiece(piece.color.other(), piece.direction.reverse())
                else -> piece
            }
        }

        private fun asOppositeSidePos(pos: BoardPos, rows: Int, cols: Int): BoardPos {
            return BoardPos.get(rows - pos.i-1, cols - pos.j-1)
        }


    }
}