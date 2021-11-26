package de.pans.main

import de.pans.command.Commands
import de.pans.dot2.Settings
import de.pans.dot2.WebSettings
import de.pans.midiio.MidiDevice
import de.pans.midiio.MidiKey
import de.pans.midiio.MidiMessage
import de.pans.webinterface.WebIO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.LinkedBlockingDeque
import javax.sound.midi.MidiUnavailableException

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
    devices.forEach {
        try {
            val device = MidiDevice(it)
            Translator.midiDevs.add(device)
        } catch (e: MidiUnavailableException) {
            System.err.println("MIDI device '$it' couldn't be loaded.")
            System.err.println("You have to 'dev load $it' to reload it.")
        }
    }
    println("Finished loading saved MIDI devices!")

    if (Settings.get("autoconnect")) {
        Commands.handle("web c")
    }

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

    private var waitingForNextInput = false
    private val nextInput = ArrayBlockingQueue<MidiMessage>(1)

    var mode = Mode.MIDI

    private var lastMidiKeyInput = MidiKey("", -1)
    private var lastFaderWebSendTime = System.currentTimeMillis()
    private val lastFaderWebSendDeque = LinkedBlockingDeque<FaderWebSend>()

    init {
        WebSettings.getAllExecutors()

        GlobalScope.launch {
            while (true) {
                val exec = lastFaderWebSendDeque.peekFirst()
                if (exec != null && System.currentTimeMillis() - exec.time > 15) {
                    WebIO.sendFaderPos(exec.exec.execID, 0, exec.exec.value)
                    lastFaderWebSendDeque.clear()
                }
            }
        }
        GlobalScope.launch {
            while (true) {
                Thread.sleep(1000 / 30L)
                if (mode != Mode.WEB) {
                    continue
                }

                val maps = WebSettings.getAllExecutors()

                for (pair in maps) {
                    val device = pair.first.device
                    var channel = pair.first.channel

                    if (channel > 128) {
                        channel -= 200
                    }

                    val state = pair.second.readStateButton
                    if (state == -1) {
                        Commands.handle("web rc")
                    }
                    if (state == 1) {
                        val type = pair.second.readTypeButton
                        var colorID = 0

                        type.let {
                            when {
                                it.isGreen -> colorID = 1
                                it.isRed -> colorID = 3
                                it.isYellow -> colorID = 5
                            }
                        }

                        device.send(0x90, channel, colorID)
                        device.send(0xB0, channel, 127)
                    } else {
                        device.send(0x90, channel, 0)
                        device.send(0xB0, channel, 0)
                    }
                }
            }
        }

    }

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
        } else if (mode == Mode.WEB) {

            val bind = WebSettings.getBind(midiMessage.midiKey)

            bind?.let {
                if (it is WebSettings.CMD) {
                    if (midiMessage.value == 0) {
                        return
                    }
                    val cmd = it.name
                    println("Sending command ${it.name}")
                    if (cmd == "clear") {
                        loopMidi.send(0x90, 21, 127)
                        loopMidi.send(0x90, 21, 0)
                        return
                    }
                    WebIO.sendCMD(it.name)
                }

                if (it is WebSettings.EXEC) {
                    it.value = midiMessage.value / 127.0

                    if (it.value == 0.0 || it.value == 1.0) {
                        lastFaderWebSendDeque.clear()
                    } else if (System.currentTimeMillis() - lastFaderWebSendTime < 7) {
                        lastFaderWebSendDeque.addFirst(FaderWebSend(it, System.currentTimeMillis()))
                        return
                    }

                    if (!it.isFader) {
                        val buttonState = it.readStateButton
                        val buttonType = it.readTypeButton

                        if (buttonType.isRed) {
                            if (it.value == 0.0) {
                                return
                            }
                            if (buttonState == 0) {
                                WebIO.sendFaderPos(it.execID, 0, 1.0)
                            } else {
                                WebIO.sendFaderPos(it.execID, 0, 0.0)
                            }
                            return
                        } else if (buttonType.isGreen) {
                            if (it.value == 0.0) {
                                return
                            }

                            WebIO.sendFaderPos(it.execID, 0, 1.0)
                            Thread.sleep(200)
                            WebIO.sendFaderPos(it.execID, 0, 0.0)
                            return
                        }
                    }

                    WebIO.sendFaderPos(it.execID, 0, it.value)
                    lastFaderWebSendTime = System.currentTimeMillis()
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

private data class FaderWebSend(val exec: WebSettings.EXEC, val time: Long)

fun printerr(msg: Any) {
    System.err.println(msg)
}
