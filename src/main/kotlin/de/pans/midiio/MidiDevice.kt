package de.pans.midiio

import de.pans.main.Translator

class MidiDevice(name: String, val isInput: Boolean = true, val isOutput: Boolean = true) {

    lateinit var input: MidiConnectionInput
    lateinit var output: MidiConnectionOutput

    val name: String

    init {
        if (isInput) {
            input = MidiConnectionInput.openConnection(name, handleInput())
        }
        if (isOutput) {
            output = MidiConnectionOutput.openConnection(name)
        }

        if (isInput) {
            this.name = input.name
        } else if (isOutput) {
            this.name = output.name
        } else {
            this.name = ""
        }
    }

    private fun handleInput(): (List<Int>) -> Unit {
        return {
            val type = it[0]
            var channel = it[1]
            var value = -1

            when (type) {
                -128 -> { // Note off
                    value = 0
                    // As APCMINI mapped some keys to the same channels and differenciates them
                    // from Note on/off and CC, im adding 200 to maintain unique channel numbers
                    channel += 200
                }
                -112 -> { // Note on
                    value = 127
                    // As APCMINI mapped some keys to the same channels and differenciates them
                    // from Note on/off and CC, im adding 200 to maintain unique channel numbers
                    channel += 200
                }
                -80 -> { // Control change / CC
                    value = it[2]
                }
            }

            Translator.onIncoming(MidiMessage(MidiKey(name, channel), value))
        }
    }

    fun lightButton(key: MidiKey, lightState: Int = 127) {
        if (key.toControllerKey.isFader) {
            return
        }

        var c = key.channel
        if (c > 128) {
            c -= 200
        }

        send(0x90, c, lightState)
        send(0xB0, c, lightState)
    }

    fun send(a: Int, b: Int, c: Int) {
        if (isOutput) {
            output.send(listOf(a, b, c))
        }
    }

    fun reload() {
        if (isOutput) {
            output.reload()
        }
        if (isInput) {
            input.reload()
        }
        println("Reloaded $name.")
    }

    fun unload() {
        if (isOutput) {
            output.close()
        }
        if (isInput) {
            input.close()
        }
    }

    override fun equals(other: Any?): Boolean {
        return other is MidiDevice && name == other.name
    }

}