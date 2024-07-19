package dev.cubxity.tools.stresscraft.module

import dev.cubxity.tools.stresscraft.StressCraft
import dev.cubxity.tools.stresscraft.data.StressCraftSession
import org.geysermc.mcprotocollib.network.packet.Packet

interface Module {
    fun register(app: StressCraft) {}

    fun tick(session: StressCraftSession) {}

    fun handlePacket(session: StressCraftSession, packet: Packet) {}
}