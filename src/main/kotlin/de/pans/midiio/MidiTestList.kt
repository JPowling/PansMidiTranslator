package de.pans.midiio

import java.util.*
import javax.sound.midi.MidiMessage
import javax.sound.midi.MidiSystem
import javax.sound.midi.Receiver

fun main(args: Array<String>) {
    val deviceInfos = MidiSystem.getMidiDeviceInfo()
    val scanner = Scanner(System.`in`)

    println("Available MIDI devices:")
    deviceInfos.forEachIndexed { index, info -> println("$index: ${info.description} $info") }

    println()
    println("Please enter ID of MIDI device to read:")
    val index = scanner.nextInt()

    println("Starting to read MIDI signals from: ${deviceInfos[index].name}")

    val device = MidiSystem.getMidiDevice(deviceInfos[index])

    val transmitter = device.transmitter
    transmitter.receiver = MidiInputReciever2()

    device.open()

    while (true) {

    }

}

class MidiInputReciever2 : Receiver {

    override fun close() {

    }

    override fun send(message: MidiMessage?, timeStamp: Long) {
        println(Arrays.toString(message!!.message))
    }

}