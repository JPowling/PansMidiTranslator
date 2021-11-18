package de.pans.midioutput

import javax.sound.midi.MidiDevice
import javax.sound.midi.MidiSystem
import javax.sound.midi.ShortMessage

class MidiOutputConnection(info: MidiDevice.Info) {

    private val device = MidiSystem.getMidiDevice(info)

    companion object {

        fun openConnection(id: Int): MidiOutputConnection {
            val info = MidiSystem.getMidiDeviceInfo()[id]
            return MidiOutputConnection(info)
        }

        fun openConnection(id: String): MidiOutputConnection {
            return openConnection(search(id))
        }

        private fun search(query: String): Int {
            return MidiSystem.getMidiDeviceInfo().indexOfFirst {
                it.name.lowercase().contains(query.lowercase()) && it.description.contains("External")
            }
        }
    }

    init {
        device.open()
    }

    fun send(bytes: List<Byte>) {
        val bytes = bytes.map { it.toInt() }
        device.receiver.send(ShortMessage(bytes[0], bytes[1], bytes[2]), -1)
    }

}