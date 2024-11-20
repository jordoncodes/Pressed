package me.onlyjordon.pressed.globalboosters

import java.util.*

class Booster(
    val ownerUUID: UUID,
    val multiplier: Double,
    private var endTime: Long
) {
    var paused = false
    private var pauseTime: Long = 0

    fun pause() {
        if (!paused) {
            pauseTime = System.currentTimeMillis()
            paused = true
        }
    }

    fun resume() {
        if (paused) {
            endTime += System.currentTimeMillis() - pauseTime
            paused = false
        }
    }

    val remainingTime: Long
        get() = endTime - System.currentTimeMillis()

    val isActive: Boolean
        get() = !paused && remainingTime > 0

}
