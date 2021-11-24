package de.pans.midiio

import javax.sound.midi.MidiDevice
import javax.sound.midi.MidiSystem
import javax.sound.midi.MidiUnavailableException
import javax.sound.midi.ShortMessage

class MidiConnectionOutput(devInfo: MidiDevice.Info) : MidiConnectionIO(devInfo) {

    val name = devInfo.name

    companion object {

        fun openConnection(id: Int): MidiConnectionOutput {
            val info = MidiSystem.getMidiDeviceInfo()[id]
            return MidiConnectionOutput(info)
        }

        fun openConnection(id: String): MidiConnectionOutput {
            try {
                return openConnection(search(id, true))
            } catch (e: ArrayIndexOutOfBoundsException) {
                throw MidiUnavailableException("MIDI device $id not found!")
            }
        }
    }

    init {
        load()
    }

    fun send(bytes: List<Int>) {
        device.receiver.send(ShortMessage(bytes[0], bytes[1], bytes[2]), -1)
    }

    fun send(vararg bytes: Int) {
        if (bytes.size == 3) {
            send(bytes.toList())
        }
    }

    fun close() {
        device.close()
    }

}