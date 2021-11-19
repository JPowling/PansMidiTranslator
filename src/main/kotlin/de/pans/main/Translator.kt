package de.pans.main

import de.pans.command.Commands
import de.pans.dot2.Dot2Mapper
import de.pans.dot2.MappingSettings
import de.pans.midiio.MidiConnectionInput
import de.pans.midiio.MidiConnectionOutput
import java.util.*

var setup = false
var suspend = false

val scanner = Scanner(System.`in`)

fun main(args: Array<String>) {
    MappingSettings // Load

    val loopMidi = MidiConnectionOutput.openConnection("loop")
    val feedback = MidiConnectionOutput.openConnection("nanokontrol")
    val input = MidiConnectionInput.openConnection("nanokontrol") {
        val bytes = Dot2Mapper.map(it)

        feedback.send(it.map { it.toInt() })

        if (!setup) {
            if (bytes.size == 3) {
                loopMidi.send(bytes.map { it.toInt() })
            }
        } else {
            when (MappingSettings.bindNext(it[1])) {
                2 -> println("You've ran out of free MIDI notes!")
            }
        }
    }

    while (true) {
        val input = scanner.nextLine()
        Commands.handle(input)
    }
}

private fun SOS() {
    println(
        """Help page from PansMidiTranslator
        |-----------------------------------
        |
        |keymap reset: reset keymap
        |keymap setup: enter setup mode (press MIDI Controller keys to assign them to next free MIDI note)
        |keymap endsetup: end setup mode
        |
        |file save: save file to file specified in separate prompt
        |file load: load file from file specified in separate prompt
        |
        |-----------------------------------
        |
    """.trimMargin()
    )
}