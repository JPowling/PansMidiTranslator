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
                if (State.isInState(State.RUN)) {
                    state = State.SETUP
                }
            }
            "endsetup" -> {
                if (State.isInState(State.SETUP)) {
                    state = State.RUN
                }
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
            "unbind" -> {
                val input = AskForMIDIInput.wait("Please press the button you want to unbind", nanoKontrol2)
                MappingSettings.unbind(input.first)
                println("Successfully unbound button ${NanoKontrol2.getByID(input.first)}")
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
            |keymap setup: Enter setup mode. Now press the buttons you wish to add to the keymap.
            |keymap endsetup: Leave setup mode.
            |keymap reset: Reset keymap.
            |keymap view: Enter/Leave viewbinds mode. Press the buttons you want to know what they are mapped to. [toggleable]
            |keymap unbind: Next MIDI Input will be unbound.
        """.trimMargin()
        )
    }
}