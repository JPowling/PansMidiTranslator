package de.pans.midiio

import de.pans.controllers.Controller
import de.pans.controllers.ControllerKey
import de.pans.main.Translator

data class MidiKey(val deviceName: String, val channel: Int) {

    val toControllerKey: ControllerKey by lazy {
        Controller.controllers.first { it.name == deviceName }.list.first { it.channel == channel }
    }

    val device: MidiDevice by lazy {
        Translator.midiDevs.first { it.name == deviceName }
    }

    override fun toString(): String {
        return "($deviceName: $toControllerKey)"
    }

}