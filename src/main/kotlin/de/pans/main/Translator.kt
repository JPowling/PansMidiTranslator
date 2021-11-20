package de.pans.main

import de.pans.command.Commands
import de.pans.controllers.NanoKontrol2
import de.pans.dot2.Dot2Mapper
import de.pans.dot2.MappingSettings
import de.pans.midiio.MidiConnectionInput
import de.pans.midiio.MidiConnectionOutput
import java.util.*

var state = State.RUN
    set(value) {
        if (field == State.VIEWBINDS) lastNanoKontrol2Input = null
        field = value
        State.updateMessage()
    }
var suspend_all = false

var lastNanoKontrol2Input: NanoKontrol2? = null
lateinit var nanoKontrol2: MidiConnectionInput

val scanner = Scanner(System.`in`)

fun main(args: Array<String>) {
    MappingSettings // Load

    val loopMidi = MidiConnectionOutput.openConnection("loop")
    val feedback = MidiConnectionOutput.openConnection("nanokontrol")
    nanoKontrol2 = MidiConnectionInput.openConnection("nanokontrol") {
        val bytes = Dot2Mapper.map(it)

        feedback.send(it)

        if (suspend_all)
            return@openConnection

        val nanoKontrol2 = NanoKontrol2.getByID(it[1])

        when (state) {
            State.RUN -> {
                if (bytes.size == 3) {
                    loopMidi.send(bytes)
                }
            }
            State.SETUP -> {
                when (val bindID = MappingSettings.bindNext(it[1])) {
                    -2 -> println("You've ran out of free MIDI notes!")
                    -1 -> {
                    }
                    else -> println("Bound MIDI Channel $bindID to $nanoKontrol2")
                }
            }
            State.VIEWBINDS -> {
                if (nanoKontrol2 != lastNanoKontrol2Input) {
                    lastNanoKontrol2Input = nanoKontrol2

                    val bind = MappingSettings.getBind(it[1])

                    if (bind == -1) {
                        println("Detected MIDI message: Button $nanoKontrol2, but it is not mapped!")
                    } else {
                        println(
                            "Detected MIDI message: Button $nanoKontrol2 " +
                                    "is mapped to MIDI channel $bind"
                        )
                    }
                }
            }
        }
    }

    while (true) {
        val input = scanner.nextLine()
        Commands.handle(input)
    }
}

enum class State {

    VIEWBINDS,
    SETUP,
    RUN,
    ;

    companion object {
        fun updateMessage() {
            when (state) {
                VIEWBINDS -> {
                    println("Entered $state state. You will now see what incoming MIDI messages are mapped to.")
                    println("NOTE: They'll NOT be translated.")
                }
                SETUP -> {
                    println("Entered $state state. Please enter MIDI inputs that you wish to add to keymap.")
                }
                RUN -> {
                    println("Entered $state state. Incoming MIDI signals will now be translated.")
                }
            }
        }

        fun sendNeedState(vararg neededState: State) {
            println("We are currently in $state state. Please ensure that you are in the ${neededState.joinToString(" or ")} state.")
        }

        fun isInState(vararg neededState: State): Boolean {
            if (neededState.contains(state)) {
                return true
            }
            sendNeedState(*neededState)
            return false
        }
    }

}