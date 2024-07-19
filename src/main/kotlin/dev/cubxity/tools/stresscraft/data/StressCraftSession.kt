package dev.cubxity.tools.stresscraft.data

import org.geysermc.mcprotocollib.protocol.MinecraftProtocol
import org.geysermc.mcprotocollib.protocol.data.game.ClientCommand
import org.geysermc.mcprotocollib.protocol.data.game.ResourcePackStatus
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.ClientboundLoginPacket
import org.geysermc.mcprotocollib.protocol.packet.common.clientbound.ClientboundResourcePackPushPacket
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.ClientboundRespawnPacket
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.entity.player.ClientboundPlayerPositionPacket
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.entity.player.ClientboundSetHealthPacket
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.level.ClientboundForgetLevelChunkPacket
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.level.ClientboundLevelChunkWithLightPacket
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.level.ClientboundSetTimePacket
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.ServerboundClientCommandPacket
import org.geysermc.mcprotocollib.protocol.packet.common.serverbound.ServerboundResourcePackPacket
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.level.ServerboundAcceptTeleportationPacket
import org.geysermc.mcprotocollib.network.Session
import org.geysermc.mcprotocollib.network.event.session.DisconnectedEvent
import org.geysermc.mcprotocollib.network.event.session.SessionAdapter
import org.geysermc.mcprotocollib.network.packet.Packet
import org.geysermc.mcprotocollib.network.tcp.TcpClientSession
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
            is ClientboundResourcePackPushPacket -> {
                app.options.acceptResourcePacks
                    ?.let { ServerboundResourcePackPacket(packet.id, it) }
                    ?.let(session::send)
            }
        }
    }

    override fun disconnected(event: DisconnectedEvent?) {
        handleDisconnect()
    }

    private fun computeKey(x: Int, z: Int): Long =
        x.toLong().shl(32) or (z.toLong().and(0xFFFFFFFFL))

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