package dev.cubxity.tools.stresscraft

data class StressCraftOptions(
    val count: Int,
    val delay: Int,
    val buffer: Int,
    val prefix: String,
    val simulate: Boolean,
    val acceptResourcePacks: Boolean
)