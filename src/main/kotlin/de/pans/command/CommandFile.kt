package de.pans.command

import de.pans.dot2.Settings
import de.pans.main.AskForConfirmation

object CommandFile : Command("file") {

    override fun handle(args: List<String>) {
        if (args.isEmpty()) {
            showHelp()
            return
        }
        when (args[0]) {
            "save" -> {
                if (args.size < 2) {
                    println("Not enough arguments!")
                }
                val filename = args.subList(1, args.size).joinToString(" ")
                Settings.saveAs(filename)
                println("Saved cache to $filename")
            }
            "load" -> {
                if (args.size < 2) {
                    println("Not enough arguments!")
                }
                val filename = args.subList(1, args.size).joinToString(" ")
                Settings.loadFrom(filename)
                println("Loaded file $filename")
            }
            "reset" -> AskForConfirmation("Proceeding will result in loss of current cache, if not saved.") {
                Settings.reset()
                println("The keymap got resetted.")
            }
        }
    }

    override fun showHelp() {
        println(
            """Usage of 'file':
            |file save <[filename]>: Saves current cache into specified file.
            |file load <[filename]>: Loads specified file into cache.
            |file reset: Reset everything.
        """.trimMargin()
        )
    }
}