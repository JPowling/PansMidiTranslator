package de.pans

import de.pans.midiio.MidiConnectionInput
import de.pans.midiio.MidiConnectionOutput

fun main(args: Array<String>) {
    val output = MidiConnectionOutput.openConnection("nano")
    val input = MidiConnectionInput.openConnection("nano") {
        println(it[1])
    }

//    println(CAPCMINI.list.filter { it.channel == 0 })

    val sleep = 1L

    output.send(0xB0, 32, 127)

//    while (true) {
//        for (i in 0..127) {
//            output.send(0x90, i, 127)
//        }
//        Thread.sleep(sleep)
//        for (i in 0..127) {
//            output.send(0x90, i, 0)
//        }
//        Thread.sleep(sleep)
//    }
}