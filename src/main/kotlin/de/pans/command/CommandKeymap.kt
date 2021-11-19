package de.pans.command

import de.pans.dot2.MappingSettings
import de.pans.main.AskForConfirmation
import de.pans.main.setup

object CommandKeymap : Command("keymap", "km") {
    override fun handle(args: List<String>) {
        if (args.isEmpty()) {
            showHelp()
            return
        }
        when (args[0]) {
            "setup" -> setup = true
            "endsetup" -> setup = false
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
        """.trimMargin()
        )
    }
}