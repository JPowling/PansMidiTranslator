package de.pans.midiinput

import javax.sound.midi.MidiDevice
import javax.sound.midi.MidiMessage
import javax.sound.midi.MidiSystem
import javax.sound.midi.Receiver

@Suppress("MemberVisibilityCanBePrivate")
class MidiInputConnection private constructor(
    val devInfo: MidiDevice.Info,
    val receiver: Receiver
) {

    private lateinit var device: MidiDevice

    companion object {

        fun openConnection(id: Int, handle: (List<Byte>) -> Unit): MidiInputConnection {
            val info = MidiSystem.getMidiDeviceInfo()[id]
            return MidiInputConnection(info, MidiInputReceiver(handle))
        }

        fun openConnection(id: String, handle: (List<Byte>) -> Unit): MidiInputConnection {
            return openConnection(search(id), handle)
        }

        fun search(query: String): Int {
            return MidiSystem.getMidiDeviceInfo().indexOfFirst {
                it.name.lowercase().contains(query.lowercase()) && !it.description.contains("External")
            }
        }

    }

    init {
        load()
    }

    fun reload() {
        unload()
        load()
    }

    fun load() {
        device = MidiSystem.getMidiDevice(devInfo)
        device.transmitter.receiver = receiver
        device.open()

        println("Loaded ${devInfo.name}")
    }

    fun unload() {
        device.transmitter.close()
        device.close()
    }

    private class MidiInputReceiver(val handle: (List<Byte>) -> Unit) : Receiver {
        override fun close() {
        }

        override fun send(message: MidiMessage?, timeStamp: Long) {
            message?.let { handle(it.message.toList()) }
        }

    }

}