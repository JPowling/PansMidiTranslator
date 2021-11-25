package de.pans.command

import de.pans.dot2.WebSettings
import de.pans.main.Translator
import de.pans.main.printerr

object CommandWebmap : Command("webmap", "wm") {
    override fun handle(args: List<String>) {
        when (args[0]) {
            "bind" -> when (args[1]) {
                "cmd" -> {
                    val command = args.subList(2, args.size).joinToString(" ")
                    val cmd = WebSettings.CMD(command)

                    println("Please press the MIDI key you want to assign the command '$command' to.")
                    val midiMsg = Translator.getNextInput()

                    val whatsBoundToInput = WebSettings.getBind(midiMsg.midiKey)
                    if (whatsBoundToInput != null) {
                        printerr("The key ${midiMsg.midiKey} is already bound to $whatsBoundToInput")
                        return
                    }

                    if (cmd.name == "clear") {
                        println("Note that in the current version, you have to set MIDI channel 21 to CMD 'clear' manually!")
                    }

                    WebSettings.bind(midiMsg.midiKey, cmd)
                    println("Successfully assigned ${midiMsg.midiKey} to CMD '$command'")
                }
                "exec" -> {

                    val execID: Int

                    try {
                        execID = args[2].toInt()
                    } catch (e: NumberFormatException) {
                        printerr("You have to provide a valid executor id!")
                        return
                    }
                    val execStoreString = "EXEC(execID=$execID)"

                    println("Please press the MIDI key you want to assign to executor '$execID' to.")
                    val midiMsg = Translator.getNextInput()

                    val whatsBoundToInput = WebSettings.getBind(midiMsg.midiKey)
                    if (whatsBoundToInput != null) {
                        printerr("The key ${midiMsg.midiKey} is already bound to $whatsBoundToInput")
                        return
                    }

                    WebSettings.bind(midiMsg.midiKey, execStoreString)
                    println("Successfully assigned ${midiMsg.midiKey} to EXEC '$execID'")
                }
            }
            "unbind" -> {
                println("Press the key you want to unbind")
                val midiMsg = Translator.getNextInput()
                val whatsBoundToInput = WebSettings.getBind(midiMsg.midiKey)

                if (whatsBoundToInput == null) {
                    printerr("The key ${midiMsg.midiKey} isn't bound yet!")
                    return
                }

                WebSettings.unbind(midiMsg.midiKey)
                println("Successfully unbound MIDI key ${midiMsg.midiKey} ($whatsBoundToInput)")
            }
        }
    }

    override fun showHelp() {
        println(
            """Usage of 'webmap':
                |wm bind CMD <[dot2 command]>: Next MIDI input will be bound to CMD.
                |wm bind EXEC <[dot2 executor id]>: Next MIDI input will be bound to EXEC.
                |wm unbind: Next MIDI input will be unbound.
        """.trimMargin()
        )
    }
}