package de.pans.command

import de.pans.dot2.Settings
import de.pans.main.*
import de.pans.midiio.MidiKey

object CommandKeymap : Command("keymap", "km") {
    override fun handle(args: List<String>) {
        if (args.isEmpty()) {
            showHelp()
            return
        }
        if (Translator.mode != Mode.MIDI) {
            printerr("You have to be in MIDI mode!")
        }
        when (args[0]) {
            "setup", "endsetup" -> {
                if (State.isInState(State.RUN, State.SETUP)) {
                    when (state) {
                        State.RUN -> state = State.SETUP
                        State.SETUP -> state = State.RUN
                        else -> {
                        }
                    }
                }
            }
            "bind" -> {
                if (args.size < 2) {
                    printerr("You have to provide a MIDI channel!")
                    return
                }
                val midiChannel = args[1].toIntOrNull()
                if (midiChannel == null || !Settings.isValidMIDIChannel(midiChannel)) {
                    printerr("The MIDI channel you've inputted is in the wrong format!")
                    return
                }

                val whatsBoundTo = Settings.getWhatsBoundTo(midiChannel)
                if (!handleMIDIChannelAlreadyBound(midiChannel, whatsBoundTo)) {
                    return
                }

                val midiInputMessage = "Please press the button you want to bind to MIDI channel $midiChannel"

                suspend_all = true
                val input = AskForMIDIInput.wait(midiInputMessage, false)
                val buttonName = input.midiKey.toControllerKey.name
                if (!handleInputAlreadyBound(input.midiKey)) {
                    suspend_all = false
                    return
                }
                suspend_all = false

                Settings.bindMIDI(input.midiKey, midiChannel)
                println("Successfully bound $buttonName to MIDI channel $midiChannel.")
            }
            "unbind" -> {
                suspend_all = true
                val input = AskForMIDIInput.wait("Please press the button you want to unbind", false)
                val buttonName = input.midiKey.toControllerKey.name

                if (Settings.unbind(input.midiKey)) {
                    println("Successfully unbound button $buttonName")
                    suspend_all = false
                    return
                }
                printerr("Button $buttonName is not bound")
                suspend_all = false
            }
            "view" -> {
                if (State.isInState(State.RUN, State.VIEWBINDS)) {
                    when (state) {
                        State.RUN -> state = State.VIEWBINDS
                        State.VIEWBINDS -> state = State.RUN
                        else -> {
                        }
                    }
                }
            }
            "listfree" -> {
                println("Unbound MIDI channels: \n" +
                        Settings
                            .freeChannels
                            .chunked(35)
                            .joinToString("\n") { it.joinToString() })
                println("-------------------------")
            }
        }
    }

    private fun handleMIDIChannelAlreadyBound(midiChannel: Int, whatsBoundTo: MidiKey?): Boolean {
        var proceedOverwrite = true
        if (whatsBoundTo != null) {
            proceedOverwrite = false
            AskForConfirmation(
                "MIDI channel $midiChannel is already bound to " +
                        "${whatsBoundTo.toControllerKey.name}. Proceeding will overwrite it."
            ) {
                proceedOverwrite = true
                Settings.unbind(Settings.getWhatsBoundTo(midiChannel)!!)
            }
        }

        return proceedOverwrite
    }

    private fun handleInputAlreadyBound(key: MidiKey): Boolean {
        var proceedUnbind = true
        if (Settings.isBound(key)) {
            proceedUnbind = false
            AskForConfirmation(
                "The button ${key.toControllerKey.name} is already bound to " +
                        "MIDI channel ${Settings.getBind(key)}! You'd have to unbind it first!"
            ) {
                proceedUnbind = true
                Settings.unbind(key)
            }
        }

        return proceedUnbind
    }

    override fun showHelp() {
        println(
            """Usage of 'keymap':
            |keymap setup: Enter/Leave setup mode. Now press the buttons you wish to add to the keymap. [toggleable]
            |keymap view: Enter/Leave viewbinds mode. Press the buttons you want to know what they are mapped to. [toggleable]
            |keymap bind <MIDI channel>: Next MIDI Input will be bound to MIDI channel
            |keymap unbind: Next MIDI Input will be unbound.
            |keymap listfree: Lists free MIDI channel.
        """.trimMargin()
        )
    }
}