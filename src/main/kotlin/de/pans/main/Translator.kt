package de.pans.main

import de.pans.dot2.Dot2Mapper
import de.pans.dot2.MappingSettings
import de.pans.midiinput.MidiInputConnection
import de.pans.midioutput.MidiOutputConnection
import java.util.*

private var setup = false
var suspend = false

val scanner = Scanner(System.`in`)

fun main(args: Array<String>) {
    val output = MidiOutputConnection.openConnection("loop")
    val input = MidiInputConnection.openConnection("nanokontrol") {
        val bytes = Dot2Mapper.map(it)

        if (!setup) {
            if (bytes.size == 3) {
                output.send(bytes)
            }
        } else {
            when (MappingSettings.bindNext(it[1])) {
                2 -> println("You've ran out of free MIDI notes!")
            }
        }
    }

    while (true) {
        when (scanner.nextLine().lowercase().trim()) {
            "help" -> {
                SOS()
            }
            "keymap reset" -> {
                AskForConfirmation("Proceeding will result in loss of current cache, if not saved.") {
                    MappingSettings.unbindAll()
                }
            }
            "keymap setup" -> {
                setup = true
            }
            "keymap endsetup" -> {
                setup = false
            }
            "file save" -> {
                MappingSettings.saveAs(AskForInput.wait())
            }
            "file load" -> {
                MappingSettings.loadFrom(AskForInput.wait())
            }
        }
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