package de.pans.controllers

open class Controller(val name: String) {

    companion object {
        val controllers = listOf(CnanoKONTROL2, CAPCMINI)
    }

    open val list = emptyList<ControllerKey>()

}