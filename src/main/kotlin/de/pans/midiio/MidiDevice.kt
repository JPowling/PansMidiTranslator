package de.pans.midiio

import de.pans.main.Translator

class MidiDevice(name: String, isInput: Boolean = true, val isOutput: Boolean = true) {

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
            val channel = it[1]
            var value = -1

            when (type) {
                0x80 -> { // Note off
                    value = 0
                }
                0x90 -> { // Note on
                    value = 127
                }
                0xB0 -> { // Control change
                    value = it[2]
                }
            }

            Translator.onIncoming(MidiMessage(MidiKey(name, channel), value))
        }
    }

    fun send(a: Int, b: Int, c: Int) {
        if (isOutput) {
            output.send(listOf(a, b, c))
        }
    }

}