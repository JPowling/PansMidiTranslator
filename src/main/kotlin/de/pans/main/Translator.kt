package de.pans.main

import de.pans.command.Commands
import de.pans.dot2.Settings
import de.pans.midiio.MidiDevice
import de.pans.midiio.MidiKey
import de.pans.midiio.MidiMessage
import java.util.*
import java.util.concurrent.ArrayBlockingQueue

var state = State.RUN
    set(value) {
        field = value
        State.updateMessage()
    }

var suspend_all = false

val scanner = Scanner(System.`in`)

fun main(args: Array<String>) {
    Settings // Load

    Translator.loopMidi = MidiDevice("loopmidi", isInput = false)

    val devices = Settings.getList<String>("midiDevs")
    devices.forEach { Translator.midiDevs.add(MidiDevice(it)) }

    while (true) {
        val input = scanner.nextLine()
        Commands.handle(input)
    }
}

enum class Mode {
    MIDI, WEB
}

object Translator {

    val midiDevs = mutableListOf<MidiDevice>()
    lateinit var loopMidi: MidiDevice

    var waitingForNextInput = false
    val nextInput = ArrayBlockingQueue<MidiMessage>(1)

    var mode = Mode.MIDI

    var lastMidiKeyInput = MidiKey("", -1)

    fun onIncoming(midiMessage: MidiMessage) {
        if (waitingForNextInput) {
            nextInput.put(midiMessage)
        }

        if (mode == Mode.MIDI) {
            midiMessage.apply { midiDevice.lightButton(midiKey, value) }

            when (state) {
                State.RUN -> {
                    loopMidi.send(0x90, Settings.getBind(midiMessage.midiKey), midiMessage.value)
                }
                State.SETUP -> {
                    when (val bindID = Settings.bindNext(midiMessage.midiKey)) {
                        -2 -> println("You've ran out of free MIDI notes!")
                        -1 -> {
                        }
                        else -> println("Bound MIDI Channel $bindID to ${midiMessage.midiKey}")
                    }
                }
                State.VIEWBINDS -> {
                    midiMessage.midiKey.let {
                        if (lastMidiKeyInput != it) {
                            lastMidiKeyInput = it

                            val bind = Settings.getBind(it)

                            if (bind == -1) {
                                println("Detected MIDI message: Button $it, but it is not mapped!")
                            } else {
                                println(
                                    "Detected MIDI message: Button $it " +
                                            "is mapped to MIDI channel $bind"
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    fun getNextInput(): MidiMessage {
        waitingForNextInput = true
        val input = nextInput.take()
        waitingForNextInput = false
        return input
    }

    fun unload(device: MidiDevice) {
        if (midiDevs.contains(device)) {
            device.unload()
            midiDevs.remove(device)
        }
    }

    fun load(device: MidiDevice): Boolean {
        if (midiDevs.contains(device)) {
            return false
        }
        return midiDevs.add(device)
    }
}

enum class State {

    VIEWBINDS,
    SETUP,
    RUN,
    ;

    companion object {
        fun updateMessage() {
            when (state) {
                VIEWBINDS -> {
                    println("Entered $state state. You will now see what incoming MIDI messages are mapped to.")
                    println("NOTE: They'll NOT be translated.")
                }
                SETUP -> {
                    println("Entered $state state. Please enter MIDI inputs that you wish to add to keymap.")
                }
                RUN -> {
                    println("Entered $state state. Incoming MIDI signals will now be translated.")
                }
            }
        }

        fun sendNeedState(vararg neededState: State) {
            println("We are currently in $state state. Please ensure that you are in the ${neededState.joinToString(" or ")} state.")
        }

        fun isInState(vararg neededState: State): Boolean {
            if (neededState.contains(state)) {
                return true
            }
            sendNeedState(*neededState)
            return false
        }
    }

}