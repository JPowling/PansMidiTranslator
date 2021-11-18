package de.pans.midiinput

import java.util.*
import javax.sound.midi.MidiSystem

fun main(args: Array<String>) {
    val deviceInfos = MidiSystem.getMidiDeviceInfo()
    val scanner = Scanner(System.`in`)

    println("Available MIDI devices:")
    deviceInfos.forEachIndexed { index, info -> println("$index: $info") }

    println()
    println("Please enter ID of MIDI device to read:")
    val index = scanner.nextInt()

    println("Starting to read MIDI signals from: ${deviceInfos[index].name}")
}