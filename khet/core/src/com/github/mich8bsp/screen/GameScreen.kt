package com.github.mich8bsp.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector3
import com.github.mich8bsp.Game
import com.github.mich8bsp.logic.BoardCell
import com.github.mich8bsp.logic.EDirection
import com.github.mich8bsp.logic.ERotationDirection
import com.github.mich8bsp.logic.GameplayManager
import com.github.mich8bsp.multiplayer.EMultiplayerMode
import ktx.app.KtxScreen
import ktx.graphics.copy
import ktx.graphics.use
import ktx.log.logger

private val log = logger<GameScreen>()

class GameScreen(private val game: Game, private val gameplayManager: GameplayManager) : KtxScreen {

    private val textureManager = TextureManager()
    // The camera ensures we can render using our target resolution
    //    pixels no matter what the screen resolution is.
    private val camera = OrthographicCamera().apply { setToOrtho(false, ScreenConfig.viewportWidth, ScreenConfig.viewportHeight) }
    // create the touchPos to store mouse click position
    private val touchPos = Vector3()
    private val cellSize = textureManager.cellSize
    private val laserDurationInSec = 2f

    override fun render(delta: Float) {
        // generally good practice to update the camera's matrices once per frame
        camera.update()
        updateGame()
        renderGame(delta)
    }

    private fun updateGame(){
        // process user input
        if (Gdx.input.justTouched()) {
            touchPos.set(Gdx.input.x.toFloat(),
                    Gdx.input.y.toFloat(),
                    0f)
            camera.unproject(touchPos)
            val cellClicked = gameplayManager.board.getCell((touchPos.y / cellSize).toInt(), (touchPos.x / cellSize).toInt())
            if(cellClicked!=null){
                gameplayManager.onCellSelected(cellClicked)
            }
        }


        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            gameplayManager.onRotate(ERotationDirection.COUNTER_CLOCKWISE)
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            gameplayManager.onRotate(ERotationDirection.CLOCKWISE)
        }

        gameplayManager.playOutOpponentMove()
    }

    private fun renderGame(delta: Float){
        // tell the SpriteBatch to render in the coordinate system specified by the camera.
        game.batch.projectionMatrix = camera.combined

        game.batch.enableBlending()
        game.batch.use {
            gameplayManager.board.getCells().forEach { cell ->
                renderCell(cell)
                if(cell.laser!=null){
                    renderLaser(cell)
                    cell.reduceLaserIntensity( delta * 100f/laserDurationInSec)
                }
            }

            renderHeader();

        }
    }

    private fun renderHeader() {
        val isPlayerTurn = gameplayManager.isPlayerTurn()
        val isGameOver = gameplayManager.isGameOver()
        if(game.multiplayerMode == EMultiplayerMode.LOCAL){
            if (isGameOver) {
                val winnerColor = gameplayManager.getWinner()
                game.font.draw(game.batch, "Game Over. $winnerColor won", 100f, ScreenConfig.viewportHeight - 50f)
            } else {
                game.font.draw(game.batch, "It's ${gameplayManager.getCurrPlayerToMove()} turn", 100f, ScreenConfig.viewportHeight - 50f)
            }
        }else {
            if (isGameOver) {
                val isWinner = gameplayManager.isWinner()
                game.font.draw(game.batch, "Game Over. ${if (isWinner) "You won!" else "You lost..."}", 100f, ScreenConfig.viewportHeight - 50f)
            } else {
                game.font.draw(game.batch, "It's ${if (isPlayerTurn) "your" else "your opponent's"} turn", 100f, ScreenConfig.viewportHeight - 50f)
            }
        }
    }

    private fun renderCell(cell: BoardCell){
        val cellX: Float = cell.pos.j.toFloat() * cellSize
        val cellY: Float = cell.pos.i.toFloat() * cellSize
        val texture: Texture? = textureManager.getTexture(cell.cellColor, cell.piece)
        val rotationDegrees: Float = if(cell.piece?.isDead() != true){
            when(cell.piece?.direction) {
                EDirection.UP -> 0f
                EDirection.RIGHT -> 270f
                EDirection.DOWN -> 180f
                EDirection.LEFT -> 90f
                else -> 0f
            }
        }else{
            0f
        }
        val textureRegion = TextureRegion(texture)
        val w = textureRegion.regionWidth.toFloat()
        val h = textureRegion.regionHeight.toFloat()
        game.batch.draw(textureRegion, cellX, cellY, w/2, h/2, w, h, 1f, 1f, rotationDegrees)

        if(cell.selected){
            val selectTexture = textureManager.selectedTexture
            val selectTextureRegion = TextureRegion(selectTexture)
            game.batch.draw(selectTextureRegion, cellX, cellY)
        }
    }

    private fun renderLaser(cell: BoardCell){
        val cellX: Float = cell.pos.j.toFloat() * cellSize
        val cellY: Float = cell.pos.i.toFloat() * cellSize
        val texture = textureManager.laserTexture
        val textureRegion = TextureRegion(texture)
        val laserIntensity: Float = cell.laser?.intensity ?: 0f
        val shouldRenderDeathTexture: Boolean = cell.piece?.isDead() ?: false
        val preDrawColor = game.batch.color.copy()
        game.batch.color = preDrawColor.copy(alpha = laserIntensity / 100f)
        game.batch.draw(textureRegion, cellX, cellY)
        if(shouldRenderDeathTexture){
            game.batch.draw(TextureRegion(textureManager.deathTexture), cellX, cellY)
        }
        game.batch.color = preDrawColor
    }

    override fun dispose() {
        log.debug { "Disposing ${this.javaClass.simpleName}" }
        textureManager.dispose()
    }
}