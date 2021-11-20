package de.pans.main

import de.pans.midiio.MidiConnectionInput

object AskForMIDIInput {

    fun wait(msg: String, midiIn: MidiConnectionInput, blockInputs: Boolean = true): Pair<Int, Int> {
        if (blockInputs) suspend_all = true
        println(msg)
        val input = midiIn.getNextInput()
        if (blockInputs) suspend_all = false
        return Pair(input[1], input[2])
    }

}