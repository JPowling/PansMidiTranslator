package de.pans.main

import de.pans.midiio.MidiConnectionInput

object AskForMIDIInput {

    fun wait(msg: String, midiIn: MidiConnectionInput): Pair<Int, Int> {
        suspend = true
        println(msg)
        val input = midiIn.getNextInput()
        suspend = false
        return Pair(input[1], input[2])
    }

}