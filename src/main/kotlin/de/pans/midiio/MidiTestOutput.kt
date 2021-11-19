package de.pans.midiio

fun main(args: Array<String>) {
    val dev = MidiConnectionOutput.openConnection("loop")

//    println(dev.devInfo.name)

//    println(receiver)

    while (true) {
        dev.send(listOf(144.toByte(), 0, 127))
        Thread.sleep(200)
        dev.send(listOf(144.toByte(), 0, 0))
    }
}