package de.pans.main

class AskForInput(val message: String) {

    fun wait(): String {
        suspend_all = true
        println(message)
        val input = scanner.nextLine()
        suspend_all = false
        return input
    }

    fun waitInt(): Int {
        suspend_all = true
        println(message)
        val input = scanner.nextInt()
        suspend_all = false
        return input
    }

}