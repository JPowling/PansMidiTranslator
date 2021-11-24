package de.pans.main

import de.pans.midiio.MidiMessage

object AskForMIDIInput {

    fun wait(msg: String, blockInputs: Boolean = true): MidiMessage {
        if (blockInputs) suspend_all = true
        println(msg)
        val input = Translator.getNextInput()
        if (blockInputs) suspend_all = false
        return input
    }

}