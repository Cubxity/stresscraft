package dev.cubxity.tools.stresscraft

import com.github.steveice10.mc.protocol.MinecraftProtocol
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.ClientboundLoginPacket
import com.github.steveice10.packetlib.Session
import com.github.steveice10.packetlib.event.session.DisconnectedEvent
import com.github.steveice10.packetlib.event.session.SessionAdapter
import com.github.steveice10.packetlib.packet.Packet
import com.github.steveice10.packetlib.tcp.TcpClientSession

class Session(private val app: StressCraft) : SessionAdapter() {
    private var wasAlive = false
    private var wasActive = false

    fun connect(host: String, port: Int, name: String) {
        val protocol = MinecraftProtocol(name)
        val session = TcpClientSession(host, port, protocol)

        session.addListener(this)

        app.sessions.incrementAndGet()
        wasAlive = true
        try {
            session.connect()
        } catch (error: Throwable) {
            handleDisconnect()
        }
    }

    override fun packetReceived(session: Session?, packet: Packet?) {
        if (packet is ClientboundLoginPacket && !wasActive) {
            app.activeSessions.incrementAndGet()
            wasActive = true
        }
    }

    override fun disconnected(event: DisconnectedEvent?) {
        handleDisconnect()
    }

    private fun handleDisconnect() {
        if (wasAlive) {
            app.sessions.decrementAndGet()
            wasAlive = false
        }
        if (wasActive) {
            app.activeSessions.decrementAndGet()
            wasActive = false
        }
    }
}