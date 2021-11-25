package de.pans.midiio

import de.pans.controllers.Controller
import de.pans.controllers.ControllerKey

data class MidiKey(val deviceName: String, val channel: Int) {

    val toControllerKey: ControllerKey by lazy {
        Controller.controllers.first { it.name == deviceName }.list.first { it.channel == channel }
    }

    override fun toString(): String {
        return "($deviceName: $toControllerKey)"
    }

    fun toStoreString(): String {
        return super.toString()
    }

}