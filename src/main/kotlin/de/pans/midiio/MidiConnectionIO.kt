package de.pans.midiio

import javax.sound.midi.MidiDevice
import javax.sound.midi.MidiSystem

open class MidiConnectionIO(val devInfo: MidiDevice.Info) {

    protected var device: MidiDevice = MidiSystem.getMidiDevice(devInfo)

    companion object {

        fun search(query: String, isOutput: Boolean): Int {
            if (isOutput) {
                return MidiSystem.getMidiDeviceInfo().indexOfFirst {
                    it.name.lowercase().contains(query.lowercase()) && it.description.isOutput()
                }
            }
            return MidiSystem.getMidiDeviceInfo().indexOfFirst {
                it.name.lowercase().contains(query.lowercase()) && !it.description.isOutput()
            }
        }

    }

    fun unload() {
        device.close()
    }

    fun reload() {
        unload()
        load()
    }

    open fun load() {
        device = MidiSystem.getMidiDevice(devInfo)
        device.open()

        println("Loaded ${devInfo.name} as ${this::class.simpleName}: ${devInfo.description}")
    }

}

private fun String.isOutput(): Boolean {
    return this.contains("External")
}
