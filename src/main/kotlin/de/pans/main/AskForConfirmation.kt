package de.pans.main

class AskForConfirmation(val confirmationMessage: String, run: () -> Unit) {

    init {
        suspend = true

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
        suspend = false
    }

    private fun sendMessage() {
        println(confirmationMessage)
        println("Do you really want to proceed? (y/n)")
    }

}