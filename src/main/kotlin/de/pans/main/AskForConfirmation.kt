package de.pans.main

class AskForConfirmation(private val confirmationMessage: String, run: () -> Unit) {

    init {
        suspend_all = true

        sendMessage()

        while (true) {
            val line = scanner.nextLine()
            if (line.lowercase() == "y") {
                println("I have warned you! Proceeding...")
                run()
                break
            } else if (line.lowercase() == "n") {
                println("Maybe this is the better choice. Aborting...")
                break
            } else {
                println("Can't you listen?")
                sendMessage()
            }
        }
        suspend_all = false
    }

    private fun sendMessage() {
        println(confirmationMessage)
        println("Do you really want to proceed? (y/n)")
    }

}