package de.pans

import de.pans.midiio.MidiConnectionInput
import de.pans.midiio.MidiConnectionOutput

fun main(args: Array<String>) {
    val output = MidiConnectionOutput.openConnection("apc")
    val input = MidiConnectionInput.openConnection("apc") {
        println(it[1])
    }

//    println(CAPCMINI.list.filter { it.channel == 0 })

    val sleep = 1L

    while (true) {
        for (i in 0..127) {
            output.send(0x90, i, 127)
        }
        Thread.sleep(sleep)
        for (i in 0..127) {
            output.send(0x90, i, 0)
        }
        Thread.sleep(sleep)
    }
}