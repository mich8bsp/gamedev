package com.github.mich8bsp.desktop

import com.badlogic.gdx.Application
import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.github.mich8bsp.Game
import com.github.mich8bsp.screen.ScreenConfig

object DesktopLauncher {
    @JvmStatic
    fun main(arg: Array<String>) {
        val config = LwjglApplicationConfiguration().apply {
            title = "Khet"
            width = ScreenConfig.screenWidth
            height = ScreenConfig.screenHeight
        }
        LwjglApplication(Game(), config).logLevel =  Application.LOG_DEBUG
    }
}