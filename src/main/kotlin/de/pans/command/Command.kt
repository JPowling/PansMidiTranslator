package de.pans.command

abstract class Command(val name: String, vararg val aliases: String) {

    abstract fun handle(args: List<String>)
    abstract fun showHelp()

    fun matches(query: String): Boolean {
        val query = query.lowercase()
        return query == name || aliases.contains(query)
    }

}

object Commands {

    private val commands = listOf(CommandFile, CommandKeymap)

    fun handle(commandLineInput: String) {
        val args = commandLineInput.trim().split(" ").toMutableList()
        val name = args[0]
        args.removeFirst()

        if (name == "help") {
            showHelp()
            return
        }

        for (command in commands) {
            if (command.matches(name)) {
                command.handle(args)
            }
        }
    }

    fun showHelp() {
        println(
            """Available Commands:
            |
            |${commands.joinToString("\n") { it.name }}
            |--------------------
        """.trimMargin()
        )
    }

}