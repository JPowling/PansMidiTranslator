package de.pans

import de.pans.midiio.MidiConnectionInput
import de.pans.midiio.MidiConnectionOutput

fun main(args: Array<String>) {
    val output = MidiConnectionOutput.openConnection("apc")
    val input = MidiConnectionInput.openConnection("nano") {
        println(it[1])
    }

//    for (i in 0..128) {
//        println(i)
//        output.send(0x90, 0x00, i)
//        Scanner(System.`in`).nextLine()
//    }
}