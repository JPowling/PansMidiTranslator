package de.pans.dot2

import de.pans.midiio.MidiKey

object Dot2Mapper {

    fun map(key: MidiKey): Int {
        return Settings.getBind(key)
    }

}