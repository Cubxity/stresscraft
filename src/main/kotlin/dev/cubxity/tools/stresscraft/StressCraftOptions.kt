package dev.cubxity.tools.stresscraft

import com.github.steveice10.mc.protocol.data.game.ResourcePackStatus

data class StressCraftOptions(
    val count: Int,
    val delay: Int,
    val buffer: Int,
    val prefix: String,
    val simulate: Boolean,
    val acceptResourcePacks: ResourcePackStatus?
)