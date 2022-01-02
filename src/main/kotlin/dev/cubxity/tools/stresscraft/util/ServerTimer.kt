package dev.cubxity.tools.stresscraft.util

class ServerTimer {
    private var lastServerTick: Long = -1
    private var lastServerTimeUpdate: Long = -1

    var mspt: Double = 50.0
        private set
    var tps: Double = 20.0
        private set

    val hasData: Boolean
        get() = lastServerTick != -1L

    fun onWorldTimeUpdate(worldTime: Long) {
        val time = System.nanoTime()
        if (lastServerTick != -1L && lastServerTimeUpdate != -1L) {
            val elapsedTicks = worldTime - lastServerTick

            if (elapsedTicks > 0) {
                mspt = (time - lastServerTimeUpdate).toDouble() / elapsedTicks.toDouble() / 1.0E6
                tps = if (mspt <= 50) 20.0 else 1000.0 / mspt
            }
        }

        lastServerTick = worldTime
        lastServerTimeUpdate = time
    }
}