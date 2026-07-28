package com.github.mich8bsp.screen


import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.github.mich8bsp.Game
import com.github.mich8bsp.logic.EPlayerColor
import com.github.mich8bsp.logic.GameplayManager
import com.github.mich8bsp.logic.Player
import com.github.mich8bsp.multiplayer.EMultiplayerMode
import com.github.mich8bsp.multiplayer.GameServerClient
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ticker
import ktx.app.KtxScreen
import ktx.graphics.use
import kotlinx.coroutines.launch
import java.util.*

class MainMenuScreen(private val game: Game) : KtxScreen {
    private val camera: OrthographicCamera = OrthographicCamera().apply { setToOrtho(false, ScreenConfig.viewportWidth, ScreenConfig.viewportHeight) }

    var joinRequested: Boolean = false
    var player: Player? = null
    var opponentReady: Boolean = false
    private val tickerChannel = ticker(delayMillis = 5_000, initialDelayMillis = 0)

    private fun checkOnOpponent() {
        GlobalScope.launch {
            for (event in tickerChannel) {
                val isReady = GameServerClient.isGameRoomReady(player!!.playerId).await()
                if (isReady) {
                    opponentReady = true
                }
            }
        }
    }

    override fun render(delta: Float) {
        camera.update();
        game.batch.projectionMatrix = camera.combined;

        game.batch.use {
            if (!joinRequested) {
                game.font.draw(game.batch, "Welcome to Khet!", 100f, 150f)
                game.font.draw(game.batch, "Tap anywhere to begin", 100f, 100f)
            } else {
                game.font.draw(game.batch, "Waiting for opponent to join...", 100f, 150f)
            }
        }

        if (player != null && opponentReady) {
            tickerChannel.cancel()
            game.addScreen(GameScreen(game, GameplayManager(player!!, game.multiplayerMode).connect()))
            game.setScreen<GameScreen>();
            game.removeScreen<MainMenuScreen>()
            dispose();
        }

        if (Gdx.input.justTouched()) {
            if (!joinRequested) {
                if (game.multiplayerMode == EMultiplayerMode.LOCAL) {
                    this@MainMenuScreen.player = Player(
                            playerId = UUID.randomUUID(),
                            roomId = UUID.randomUUID(),
                            color = EPlayerColor.GREY
                    )
                    opponentReady = true
                } else {
                    GlobalScope.launch {
                        val player = GameServerClient.joinGameAsync().await()
                        this@MainMenuScreen.player = player
                        checkOnOpponent()
                    }
                }
            }
            joinRequested = true

        }
    }
}