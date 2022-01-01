package dev.cubxity.tools.stresscraft

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.optional
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class StressCraft(
    private val host: String,
    private val port: Int,
    private val count: Int,
    private val delay: Int,
    private val buffer: Int,
    private val prefix: String
) {
    private val executor = Executors.newScheduledThreadPool(2)
    private val terminal = Terminal()
    private var id = 0

    val sessions = AtomicInteger()
    val activeSessions = AtomicInteger()

    fun start() {
        Runtime.getRuntime().addShutdownHook(Thread {
            executeShutdownHook()
        })

        executor.scheduleAtFixedRate({
            val sessions = sessions.get()
            val activeSessions = activeSessions.get()
            if (sessions < count && sessions - activeSessions < buffer) {
                createSession()
            }
        }, 0, delay.toLong(), TimeUnit.MILLISECONDS)

        // Render at 10 FPS
        // Some terminals may be too slow to handle high frequency updates
        executor.scheduleAtFixedRate({
            renderProgress()
        }, 0, 100, TimeUnit.MILLISECONDS)
    }

    private fun createSession() {
        val name = prefix + "${id++}".padStart(4, '0')
        Session(this).connect(host, port, name)
    }

    private fun renderProgress() {
        terminal.renderHeader(host, port)
        terminal.renderBar(sessions.get(), count, "Connections")
        terminal.renderBar(activeSessions.get(), count, "Players")
        terminal.renderBar(100, 100, "Michael Appreciation")
        terminal.reset()
    }

    private fun executeShutdownHook() {
        executor.shutdownNow()
        terminal.close()
    }
}

fun main(args: Array<String>) {
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

    parser.parse(args)

    StressCraft(host, port, count, delay, buffer, prefix).start()
}
