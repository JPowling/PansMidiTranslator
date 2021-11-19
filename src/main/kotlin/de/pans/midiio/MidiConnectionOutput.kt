package de.pans.midiio

import javax.sound.midi.MidiDevice
import javax.sound.midi.MidiSystem
import javax.sound.midi.ShortMessage

class MidiConnectionOutput(devInfo: MidiDevice.Info) : MidiConnectionIO(devInfo) {

    companion object {

        fun openConnection(id: Int): MidiConnectionOutput {
            val info = MidiSystem.getMidiDeviceInfo()[id]
            return MidiConnectionOutput(info)
        }

        fun openConnection(id: String): MidiConnectionOutput {
            return openConnection(search(id, true))
        }
    }

    init {
        load()
    }

    fun send(bytes: List<Int>) {
        device.receiver.send(ShortMessage(bytes[0], bytes[1], bytes[2]), -1)
    }

}