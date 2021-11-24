package de.pans.midiio

import de.pans.main.Translator

data class MidiMessage(val midiKey: MidiKey, val value: Int) {

    val midiDevice: MidiDevice by lazy {
        Translator.midiDevs.first { it.name == midiKey.deviceName }
    }

}