package de.pans.midiinput

import de.pans.dot2.Dot2Mapper
import java.util.*

fun main(args: Array<String>) {

    val con = MidiInputConnection.openConnection("nanokontrol") {
        println(Dot2Mapper.map(it))
    }

    val scanner = Scanner(System.`in`)

    while (true) {
        scanner.nextLine()
        con.reload()
    }
}