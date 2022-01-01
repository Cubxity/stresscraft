package dev.cubxity.tools.stresscraft

import org.fusesource.jansi.Ansi
import org.fusesource.jansi.AnsiConsole
import java.io.Closeable
import kotlin.math.min

class Terminal : Closeable {
    private val width = 40
    private var dy = 0

    init {
        System.setProperty(AnsiConsole.JANSI_MODE, AnsiConsole.JANSI_MODE_FORCE)
        AnsiConsole.systemInstall()
        print("\u001b[?25l") // Hide cursor
    }

    fun renderHeader(host: String, port: Int) {
        val ansi = Ansi.ansi()
            .eraseLine()
            .fg(Ansi.Color.YELLOW)
            .a("\uD83D\uDE80 StressCraft by Cubxity ")
            .fg(Ansi.Color.BLUE)
            .a("[$host:$port]\n")
            .reset()
        print(ansi)
        dy++
    }

    fun renderBar(value: Int, total: Int, label: String) {
        val fill = (min(value, total) / total.toDouble() * width).toInt()
        val ansi = Ansi.ansi()
            .eraseLine()
            .fg(Ansi.Color.CYAN)
            .a("\u2588".repeat(fill) + "\u2591".repeat(width - fill))
            .reset()
            .a(" - $value/$total $label\n")
        print(ansi)
        dy++
    }

    fun reset() {
        val ansi = Ansi.ansi()
            .cursorMove(0, -dy)
            .cursorToColumn(1)
        dy = 0
        print(ansi)
    }

    override fun close() {
        print("\u001b[?25h") // Show cursor
        AnsiConsole.systemUninstall()
    }
}
