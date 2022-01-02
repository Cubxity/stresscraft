package dev.cubxity.tools.stresscraft.module

import com.github.steveice10.packetlib.packet.Packet
import dev.cubxity.tools.stresscraft.StressCraft
import dev.cubxity.tools.stresscraft.data.StressCraftSession

interface Module {
    fun register(app: StressCraft) {}

    fun tick(session: StressCraftSession) {}

    fun handlePacket(session: StressCraftSession, packet: Packet) {}
}