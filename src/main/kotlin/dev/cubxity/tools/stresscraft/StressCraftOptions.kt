package dev.cubxity.tools.stresscraft

import org.geysermc.mcprotocollib.protocol.data.game.ResourcePackStatus

data class StressCraftOptions(
    val count: Int,
    val delay: Int,
    val buffer: Int,
    val prefix: String,
    val simulate: Boolean,
    val acceptResourcePacks: ResourcePackStatus?
)