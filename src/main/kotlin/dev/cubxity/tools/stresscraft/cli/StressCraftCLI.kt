package dev.cubxity.tools.stresscraft.cli

import dev.cubxity.tools.stresscraft.StressCraft
import dev.cubxity.tools.stresscraft.StressCraftOptions
import dev.cubxity.tools.stresscraft.util.Terminal
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.optional
import kotlinx.coroutines.delay

object StressCraftCLI {
    private val terminal = Terminal()

    suspend fun main(args: Array<String>) {
        val parser = ArgParser("stresscraft")
        val host by parser.argument(ArgType.String, description = "the IP address or the hostname of the server")
        val port by parser.argument(ArgType.Int, description = "the port of the server")
            .optional().default(25565)
        val count by parser.option(ArgType.Int, "count", "c", description = "the amount of bots")
            .default(500)
        val delay by parser.option(ArgType.Int, "delay", "d", description = "delay between connections, in ms")
            .default(20)
        val buffer by parser.option(ArgType.Int, "buffer", "b", description = "buffer between connections and players")
            .default(20)
        val prefix by parser.option(ArgType.String, "prefix", "p", description = "player name prefix")
            .default("Player")
        val simulate by parser.option(
            ArgType.Boolean,
            "simulate",
            "s",
            description = "use player simulation (not implemented)"
        ).default(true)
        val acceptResourcePacks by parser.option(
            ArgType.Boolean,
            "accept_resource_packs",
            "a",
            description = "accept/deny resource packs"
        )

        parser.parse(args)

        Runtime.getRuntime().addShutdownHook(Thread {
            terminal.close()
        })
        terminal.init()

        val options = StressCraftOptions(count, delay, buffer, prefix, simulate, acceptResourcePacks)
        val app = StressCraft(host, port, options)
        app.start()

        while (true) {
            try {
                renderProgress(app)
            } catch (error: Throwable) {
                // ?
            }
            delay(100)
        }
    }

    private fun renderProgress(app: StressCraft) {
        terminal.renderHeader(app.host, app.port)
        terminal.renderGauge("\uD83D\uDCE6 Chunks", app.chunksLoaded.get())
        terminal.newLine()
        terminal.renderProgress(app.sessionCount.get(), app.options.count, "Connections")
        terminal.renderProgress(app.activeSessions.get(), app.options.count, "Players")
        terminal.renderProgress(calculateAverageTPS(app), 20.0, "TPS (Avg)*")
        terminal.renderProgress(100, 100, "Michael Appreciation")
        terminal.reset()
    }

    private fun calculateAverageTPS(app: StressCraft): Double {
        var count = 0
        var total = 0.0
        for (session in app.sessions) {
            if (session.timer.hasData) {
                count++
                total += session.timer.tps
            }
        }

        return total / count.toDouble()
    }
}


suspend fun main(args: Array<String>) {
    StressCraftCLI.main(args)
}