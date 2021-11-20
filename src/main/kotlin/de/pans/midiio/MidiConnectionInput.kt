package de.pans.midiio

import javax.sound.midi.*

@Suppress("MemberVisibilityCanBePrivate")
class MidiConnectionInput private constructor(
    devInfo: MidiDevice.Info,
    val receiver: Receiver
) : MidiConnectionIO(devInfo) {

    companion object {

        fun openConnection(id: Int, handle: (List<Byte>) -> Unit): MidiConnectionInput {
            val info = MidiSystem.getMidiDeviceInfo()[id]
            return MidiConnectionInput(info, MidiInputReceiver(handle))
        }

        fun openConnection(id: String, handle: (List<Byte>) -> Unit): MidiConnectionInput {
            try {
                return openConnection(search(id, false), handle)
            } catch (e: ArrayIndexOutOfBoundsException) {
                throw MidiUnavailableException("MIDI device $id not found!")
            }
        }

    }

    init {
        load()
    }

    override fun load() {
        super.load()
        device.transmitter.receiver = receiver
    }

    private class MidiInputReceiver(val handle: (List<Byte>) -> Unit) : Receiver {
        override fun close() {
        }

        override fun send(message: MidiMessage?, timeStamp: Long) {
            message?.let { handle(it.message.toList()) }
        }

    }

}