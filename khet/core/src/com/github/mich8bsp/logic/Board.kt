package com.github.mich8bsp.logic

import kotlin.math.abs

class Board(playerColor: EPlayerColor){

    private val boardConfig: EBoardConfigurations = when (playerColor) {
        EPlayerColor.GREY -> EBoardConfigurations.CLASSIC_GREY
        EPlayerColor.RED -> EBoardConfigurations.CLASSIC_RED
    }

    private val piecesConfiguration: Map<BoardPos, Piece> = boardConfig.configuration

    private val rows: Int = boardConfig.getBoardSize().first
    private val cols: Int = boardConfig.getBoardSize().second

    private val pharaohHealth: MutableMap<EPlayerColor, Boolean> = mutableMapOf(
            EPlayerColor.GREY to true,
            EPlayerColor.RED to true
    )

    private val cells: Array<Array<BoardCell>> = Array(rows) { i -> Array(cols) { j ->
       BoardCell.create(BoardPos.get(i, j), piecesConfiguration[BoardPos.get(i, j)], rows, cols, playerColor)
    } }

    fun makeMove(move: Move) {
        when(move){
            is RotationMove -> {
                val cell: BoardCell = cells[move.pos.i][move.pos.j]
                cell.piece?.rotate(move.direction)
            }
            is PositionMove -> {
                val fromCell: BoardCell = cells[move.from.i][move.from.j]
                val toCell: BoardCell = cells[move.to.i][move.to.j]
                toCell.piece = fromCell.piece
                fromCell.piece = null
            }
            is SwitchMove -> {
                val cell1: BoardCell = cells[move.pos1.i][move.pos1.j]
                val cell2: BoardCell = cells[move.pos2.i][move.pos2.j]
                val tmp: Piece? = cell1.piece
                cell1.piece = cell2.piece
                cell2.piece = tmp
            }
        }
    }

    fun getCells(): List<BoardCell> {
        return cells.flatten()
    }

    fun getCell(x: Int, y: Int): BoardCell? {
        return if(x<0 || x>=rows || y<0 || y>=cols){
            null
        }else{
            cells[x][y]
        }
    }

    fun getCell(pos: BoardPos): BoardCell? {
        return getCell(pos.i, pos.j)
    }

    fun getNeighborCell(cell: BoardCell, direction: EDirection): BoardCell? {
        val neighborPos: BoardPos = BoardPos.getNeighbor(cell.pos, direction)
        return getCell(neighborPos)
    }

    fun fireLaser(laserOfPlayer: EPlayerColor) {
        val sphinx1Cell = getCell(0, cols-1)
        val sphinx2Cell = getCell(rows-1, 0)
        val startCell = if(sphinx1Cell?.piece?.color == laserOfPlayer){
            sphinx1Cell
        }else{
            sphinx2Cell
        }
        if(startCell!=null){
            fireLaser(startCell)
        }else{
            println("couldn't find cell of sphinx to fire laser from")
        }
    }

    fun fireLaser(origin: BoardCell) {
        origin.activateLaser()
        var currCell = getNeighborCell(origin, origin.piece!!.direction)
        var laserDirection: EDirection? = origin.piece?.direction
        while(currCell!=null){
           currCell.activateLaser()
            if(currCell.piece!=null && laserDirection!=null){
                laserDirection = currCell.piece?.hitWithRay(laserDirection.reverse())
            }
            if(currCell.piece?.isDead() == true){
                if(currCell.piece is PharaohPiece){
                    pharaohHealth[currCell.piece!!.color] = false
                }
            }
            currCell = if(laserDirection!=null){
                getNeighborCell(currCell, laserDirection)
            }else{
                null
            }
        }
    }

    fun isPharaohDead(): Boolean {
        return !pharaohHealth.values.fold(true) {  acc, curr  -> acc && curr }
    }

    fun getWinner(): EPlayerColor? {
        return when {
            pharaohHealth[EPlayerColor.GREY] == false -> {
                EPlayerColor.RED
            }
            pharaohHealth[EPlayerColor.RED] == false -> {
                EPlayerColor.GREY
            }
            else -> {
                null
            }
        }
    }
}

class BoardCell(val pos: BoardPos, val cellColor: EPlayerColor?){
    var piece: Piece? = null
    var laser: Laser? = null
    var selected: Boolean = false

    fun isEmpty(): Boolean {
        return piece == null
    }

    fun select() {
        selected = true
    }

    fun deselect(){
        selected = false
    }

    fun activateLaser() {
        laser = Laser()
        println("imma firin mah lazor")
    }

    fun reduceLaserIntensity(delta: Float){
        laser?.dropIntensity(delta)
        if(laser?.intensity ?: 0f <= 0f){
            laser = null
            if(piece!=null && piece!!.isDead()){
                piece = null
            }
        }
    }

    companion object {
        private fun getEmptyCellColor(pos: BoardPos, boardRows: Int, boardCols: Int, playerColor: EPlayerColor): EPlayerColor? {
            if(pos.j == boardCols-1){
                return playerColor
            }
            if(pos.j == 0){
                return playerColor.other()
            }
            if(pos.j == 1 && (pos.i == 0 || pos.i == boardRows-1)){
                return playerColor
            }
            if(pos.j == boardCols-2 && (pos.i == 0 || pos.i == boardRows-1)){
                return playerColor.other()
            }
            return null;
        }

        fun create(pos: BoardPos, piece: Piece?, boardRows: Int, boardCols: Int, playerColor: EPlayerColor): BoardCell {
            val cellColor: EPlayerColor? = getEmptyCellColor(pos, boardRows, boardCols, playerColor)

            val cell = BoardCell(pos, cellColor)
            if(piece!=null){
                cell.piece = piece
            }
            return cell
        }
    }
}

data class BoardPos(val i: Int, val j: Int){
    companion object {
        fun get(i: Int, j: Int): BoardPos = BoardPos(i, j) //object pooling would be a good idea here
        fun areNeighbors(pos1: BoardPos, pos2: BoardPos): Boolean {
            val diffX = abs(pos1.i - pos2.i)
            val diffY = abs(pos1.j - pos2.j)
            return (diffX + diffY > 0) && diffX<=1 && diffY<=1
        }

        fun getNeighbor(pos: BoardPos, direction: EDirection): BoardPos {
            return when(direction){
                EDirection.DOWN -> get(pos.i-1, pos.j)
                EDirection.RIGHT -> get(pos.i, pos.j+1)
                EDirection.LEFT -> get(pos.i, pos.j-1)
                EDirection.UP -> get(pos.i+1, pos.j)
            }
        }
    }
}