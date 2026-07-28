package com.github.mich8bsp.logic

class Laser(maxIntensity: Float = 100F) {
    var intensity: Float = maxIntensity

    fun dropIntensity(delta: Float) {
        intensity -= delta
    }
}