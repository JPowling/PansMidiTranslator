package de.pans.midiio

import java.util.concurrent.ArrayBlockingQueue
import javax.sound.midi.*
import javax.sound.midi.MidiDevice
import javax.sound.midi.MidiMessage

@Suppress("MemberVisibilityCanBePrivate")
class MidiConnectionInput private constructor(
    devInfo: MidiDevice.Info,
    val receiver: MidiInputReceiver
) : MidiConnectionIO(devInfo) {

    val name = devInfo.name

    companion object {

        fun openConnection(id: Int, handle: (List<Int>) -> Unit): MidiConnectionInput {
            val info = MidiSystem.getMidiDeviceInfo()[id]
            return MidiConnectionInput(info, MidiInputReceiver(handle))
        }

        fun openConnection(id: String, handle: (List<Int>) -> Unit): MidiConnectionInput {
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

    fun getNextInput(): List<Int> {
        receiver.apply {
            isWaiting = true
            val list = queue.take()
            isWaiting = false
            return list
        }
    }

    class MidiInputReceiver(val handle: (List<Int>) -> Unit) : Receiver {
        val queue = ArrayBlockingQueue<List<Int>>(1)
        var isWaiting = false

        override fun close() {
        }

        override fun send(message: MidiMessage?, timeStamp: Long) {
            message?.let {
                val bytes = it.message.toList().map { it.toInt() }

                if (isWaiting) {
                    queue.put(bytes)
                }
                handle(bytes)
            }
        }

    }

}