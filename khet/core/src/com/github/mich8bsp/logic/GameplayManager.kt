package com.github.mich8bsp.logic

import com.github.mich8bsp.multiplayer.EMultiplayerMode
import com.github.mich8bsp.multiplayer.GameServerClient
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.launch

class GameplayManager(private val player: Player,
                    private val multiplayerMode: EMultiplayerMode) {

    private var gameOver: Boolean = false
    private val playerColor: EPlayerColor = player.color
    private var currPlayerToMove: EPlayerColor = EPlayerColor.GREY

    val board: Board = Board(playerColor)
    private val moveValidator = MoveValidator(board)
    private val latestMoveLock = Object()
    private var lastMoveRecordCache: MoveRecord<out Move>? = null
    private var lastMovePlayedOutId: Long = 0

    private var cellSelected: BoardCell? = null

    fun connect(): GameplayManager {
        if(multiplayerMode != EMultiplayerMode.LOCAL) {
            val tickerChannel = ticker(delayMillis = 5_000, initialDelayMillis = 0)

            GlobalScope.launch {
                for (event in tickerChannel) {
                    val latestMoveRecord = GameServerClient.getLatestMove(player.playerId).await()
                    if (latestMoveRecord != null) {
                        synchronized(latestMoveLock) {
                            val isNewMove: Boolean = lastMoveRecordCache == null || lastMoveRecordCache!!.recordId < latestMoveRecord.recordId
                            if (isNewMove) {
                                lastMoveRecordCache = latestMoveRecord
                            }
                        }
                    }
                }
            }
        }
        return this
    }

    fun playOutOpponentMove() {
        synchronized(latestMoveLock) {
            val isOpponentMove: Boolean = lastMoveRecordCache?.playerColor == playerColor.other()
            val isNewMove: Boolean = lastMoveRecordCache != null && lastMoveRecordCache!!.recordId > lastMovePlayedOutId
            val shouldPlayMove: Boolean = isOpponentMove && isNewMove
            if (shouldPlayMove) {
                board.makeMove(lastMoveRecordCache!!.move)
                lastMovePlayedOutId = lastMoveRecordCache!!.recordId
                onMoveFinished(lastMoveRecordCache!!.playerColor)
            }
        }
    }

    fun isPlayerAllowedToMove(): Boolean {
        val isNetworkOpponentMove: Boolean = multiplayerMode==EMultiplayerMode.NETWORK && (currPlayerToMove!=playerColor)
        return !gameOver && !isNetworkOpponentMove
    }

    fun onCellSelected(cell: BoardCell) {
        if (!isPlayerAllowedToMove()) {
            return
        }
        cellSelected = when (cellSelected) {
            cell -> {
                cell.deselect()
                null
            }
            null -> {
                if (cell.piece != null && cell.piece?.color == currPlayerToMove) {
                    cell.select()
                    cell
                } else {
                    null
                }
            }
            else -> {
                val move = if (cellSelected?.piece is ScarabPiece && !cell.isEmpty()) {
                    SwitchMove(cellSelected!!.pos, cell.pos)
                } else {
                    PositionMove(cellSelected!!.pos, cell.pos)
                }
                if (moveValidator.validateMove(move)) {
                    board.makeMove(move)
                    if(multiplayerMode==EMultiplayerMode.NETWORK) {
                        GameServerClient.sendMove(move, player.playerId)
                    }
                    onMoveFinished(currPlayerToMove)
                } else {
                    println("Invalid move")
                }
                cellSelected!!.deselect()
                null
            }
        }

    }

    fun onRotate(direction: ERotationDirection) {
        if (!isPlayerAllowedToMove()) {
            return
        }
        val isRotationAllowed = cellSelected != null && cellSelected?.piece != null && cellSelected?.piece?.color == currPlayerToMove
        if (isRotationAllowed) {
            val move = RotationMove(cellSelected!!.pos, direction)
            if (moveValidator.validateMove(move)) {
                board.makeMove(move)
                if(multiplayerMode == EMultiplayerMode.NETWORK) {
                    GameServerClient.sendMove(move, player.playerId)
                }
                onMoveFinished(currPlayerToMove)
            } else {
                println("Invalid move")
            }
            cellSelected?.deselect()
            cellSelected = null
        }
    }

    fun onMoveFinished(moveOfPlayer: EPlayerColor) {
        board.fireLaser(moveOfPlayer)
        if (board.isPharaohDead()) {
            val winner = board.getWinner()
            println("Game Over! $winner won!")
            gameOver = true
        }
        currPlayerToMove = moveOfPlayer.other()
    }

    fun isPlayerTurn(): Boolean {
        return currPlayerToMove == playerColor
    }

    fun getCurrPlayerToMove(): EPlayerColor {
        return currPlayerToMove
    }

    fun isGameOver(): Boolean {
        return gameOver
    }

    fun isWinner(): Boolean {
        return if(!isGameOver()){
            false
        }else{
            board.getWinner() == playerColor
        }
    }

    fun getWinner(): EPlayerColor? {
        return if(!isGameOver()){
            null
        }else{
            board.getWinner()
        }
    }

}