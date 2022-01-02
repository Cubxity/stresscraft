package dev.cubxity.tools.stresscraft

import dev.cubxity.tools.stresscraft.data.StressCraftSession
import dev.cubxity.tools.stresscraft.module.Module
import kotlinx.coroutines.*
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.ceil
import kotlin.system.measureNanoTime

class StressCraft(
    val host: String,
    val port: Int,
    val options: StressCraftOptions
) {
    private val coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var id = 0

    private val _sessions = LinkedList<StressCraftSession>()
    private val modules = listOf<Module>()

    val sessionCount = AtomicInteger()
    val activeSessions = AtomicInteger()
    val chunksLoaded = AtomicInteger()

    val sessions: List<StressCraftSession>
        get() = _sessions

    fun start() {
        Runtime.getRuntime().addShutdownHook(Thread {
            executeShutdownHook()
        })

        coroutineScope.launch {
            while (true) {
                try {
                    val sessions = sessionCount.get()
                    val activeSessions = activeSessions.get()
                    if (sessions < options.count && sessions - activeSessions < options.buffer) {
                        createSession()
                    }
                } catch (error: Throwable) {
                    // ?
                }
                delay(options.delay.toLong())
            }
        }

        // Ticking
        coroutineScope.launch {
            while (true) {
                val time = measureNanoTime {
                    try {
                        for (session in _sessions) {
                            for (module in modules) {
                                module.tick(session)
                            }
                        }
                    } catch (error: Throwable) {
                        // ?
                    }
                }
                delay(50 - ceil(time / 1E6).toLong())
            }
        }
    }

    fun removeSession(session: StressCraftSession) {
        synchronized(_sessions) {
            _sessions.remove(session)
        }
    }

    private fun createSession() {
        val name = options.prefix + "${id++}".padStart(4, '0')
        val session = StressCraftSession(this)
        synchronized(_sessions) {
            _sessions.add(session)
        }
        session.connect(name)
    }

    private fun executeShutdownHook() {
        coroutineScope.coroutineContext.job.cancel()
    }
}
