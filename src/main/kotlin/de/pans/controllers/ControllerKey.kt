package de.pans.controllers

data class ControllerKey(val name: String, val channel: Int) {
    override fun toString(): String {
        return name
    }
}