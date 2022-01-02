package dev.cubxity.tools.stresscraft.data

import com.github.steveice10.mc.protocol.MinecraftProtocol
import com.github.steveice10.mc.protocol.data.game.ClientCommand
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.ClientboundLoginPacket
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.ClientboundRespawnPacket
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.entity.player.ClientboundPlayerPositionPacket
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.entity.player.ClientboundSetHealthPacket
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.level.ClientboundForgetLevelChunkPacket
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.level.ClientboundLevelChunkWithLightPacket
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.level.ClientboundSetTimePacket
import com.github.steveice10.mc.protocol.packet.ingame.serverbound.ServerboundClientCommandPacket
import com.github.steveice10.mc.protocol.packet.ingame.serverbound.level.ServerboundAcceptTeleportationPacket
import com.github.steveice10.packetlib.Session
import com.github.steveice10.packetlib.event.session.DisconnectedEvent
import com.github.steveice10.packetlib.event.session.SessionAdapter
import com.github.steveice10.packetlib.packet.Packet
import com.github.steveice10.packetlib.tcp.TcpClientSession
import dev.cubxity.tools.stresscraft.StressCraft
import dev.cubxity.tools.stresscraft.util.ServerTimer

class StressCraftSession(private val app: StressCraft) : SessionAdapter() {
    private var wasAlive = false
    private var wasActive = false
    private var previousChunkCount = 0

    private val chunks = HashSet<Long>()
    private var _session: Session? = null

    val timer = ServerTimer()

    val session: Session
        get() = _session ?: error("session has not initialized")

    fun connect(name: String) {
        val protocol = MinecraftProtocol(name)
        val session = TcpClientSession(app.host, app.port, protocol)

        session.addListener(this)

        app.sessionCount.incrementAndGet()
        wasAlive = true
        try {
            _session = session
            session.connect()
        } catch (error: Throwable) {
            handleDisconnect()
        }
    }

    override fun packetReceived(session: Session, packet: Packet) {
        when (packet) {
            is ClientboundLoginPacket -> {
                if (!wasActive) {
                    app.activeSessions.incrementAndGet()
                    wasActive = true
                }
            }
            is ClientboundRespawnPacket -> {
                chunks.clear()
                app.chunksLoaded.addAndGet(-previousChunkCount)
                previousChunkCount = 0
            }
            is ClientboundSetHealthPacket -> {
                if (packet.health <= 0) {
                    session.send(ServerboundClientCommandPacket(ClientCommand.RESPAWN))
                }
            }
            is ClientboundPlayerPositionPacket -> {
                session.send(ServerboundAcceptTeleportationPacket(packet.teleportId))
            }
            is ClientboundLevelChunkWithLightPacket -> {
                chunks.add(computeKey(packet.x, packet.z))

                val size = chunks.size
                app.chunksLoaded.addAndGet(size - previousChunkCount)
                previousChunkCount = size
            }
            is ClientboundForgetLevelChunkPacket -> {
                chunks.remove(computeKey(packet.x, packet.z))

                val size = chunks.size
                app.chunksLoaded.addAndGet(size - previousChunkCount)
                previousChunkCount = size
            }
            is ClientboundSetTimePacket -> {
                timer.onWorldTimeUpdate(packet.time)
            }
        }
    }

    override fun disconnected(event: DisconnectedEvent?) {
        handleDisconnect()
    }

    private fun computeKey(x: Int, z: Int): Long =
        x.toLong().shl(32) or z.toLong()

    private fun handleDisconnect() {
        app.removeSession(this)
        if (wasAlive) {
            app.sessionCount.decrementAndGet()
            wasAlive = false
        }
        if (wasActive) {
            app.activeSessions.decrementAndGet()
            wasActive = false
        }
        chunks.clear()
        app.chunksLoaded.addAndGet(-previousChunkCount)
        previousChunkCount = 0
        _session = null
    }
}