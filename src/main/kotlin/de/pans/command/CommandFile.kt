package de.pans.command

import de.pans.dot2.MappingSettings

object CommandFile : Command("file") {

    override fun handle(args: List<String>) {
        if (args.size < 2) {
            showHelp()
            return
        }
        when (args[0]) {
            "save" -> MappingSettings.saveAs(args[1])
            "load" -> MappingSettings.loadFrom(args[1])
        }
    }

    override fun showHelp() {
        println(
            """Usage of 'file':
            |file save <[filename]>: Saves current cache into specified file
            |file load <[filename]>: Loads specified file into cache
        """.trimMargin()
        )
    }
}