package de.pans.command

import de.pans.controllers.NanoKontrol2
import de.pans.dot2.MappingSettings
import de.pans.main.*

object CommandKeymap : Command("keymap", "km") {
    override fun handle(args: List<String>) {
        if (args.isEmpty()) {
            showHelp()
            return
        }
        when (args[0]) {
            "setup" -> {
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
                val input = AskForMIDIInput.wait("Please press the button you want to bind", nanoKontrol2).first
                val buttonName = NanoKontrol2.getByID(input)

                if (MappingSettings.isBound(input)) {
                    println("The button $buttonName is already bound! You have to unbind it first!")
                    return
                }

                val askForInput =
                    AskForInput("Please type the MIDI channel number that you want $buttonName to be bound to.")
                val int = askForInput.waitInt()

                if (!MappingSettings.isValidMIDIChannel(int)) {
                    println("The MIDI channel $int is invalid. Aborting...")
                    return
                }

                var proceed = true
                val whatsBoundTo = MappingSettings.getWhatsBoundTo(int)
                if (whatsBoundTo != -1) {
                    proceed = false
                    scanner.nextLine()
                    AskForConfirmation(
                        "MIDI channel $int is already bound to " +
                                "${NanoKontrol2.getByID(whatsBoundTo)}. Overwrite?"
                    ) {
                        proceed = true
                    }
                }

                if (!proceed) {
                    return
                }

                MappingSettings.bind(input, int, true)
                println("Successfully bound $buttonName to MIDI channel $int.")
            }
            "unbind" -> {
                suspend_all = true
                val input = AskForMIDIInput.wait("Please press the button you want to unbind", nanoKontrol2, false)

                if (MappingSettings.unbind(input.first)) {
                    println("Successfully unbound button ${NanoKontrol2.getByID(input.first)}")
                    suspend_all = false
                    return
                }
                println("Button ${NanoKontrol2.getByID(input.first)} is not bound")
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
                        MappingSettings
                            .freeChannels
                            .chunked(35)
                            .joinToString("\n") { it.joinToString() })
                println("-------------------------")
            }
            "reset" -> AskForConfirmation("Proceeding will result in loss of current cache, if not saved.") {
                MappingSettings.unbindAll()
                println("The keymap got resetted.")
            }
        }
    }

    override fun showHelp() {
        println(
            """Usage of 'keymap':
            |keymap setup: Enter/Leave setup mode. Now press the buttons you wish to add to the keymap. [toggleable]
            |keymap reset: Reset keymap.
            |keymap view: Enter/Leave viewbinds mode. Press the buttons you want to know what they are mapped to. [toggleable]
            |keymap bind: Next MIDI Input will be bound to next text input
            |keymap unbind: Next MIDI Input will be unbound.
            |keymap listfree: Lists free MIDI channel.
        """.trimMargin()
        )
    }
}