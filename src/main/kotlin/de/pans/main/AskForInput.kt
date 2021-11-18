package de.pans.main

object AskForInput {

    fun wait(): String {
        suspend = true
        println("Please enter an argument: (you should know what the context is, i don't)")
        val input = scanner.nextLine()
        suspend = false
        return input
    }

}