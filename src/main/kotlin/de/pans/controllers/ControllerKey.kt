package de.pans.controllers

data class ControllerKey(val name: String, val channel: Int, val isFader: Boolean) {
    override fun toString(): String {
        return name
    }
}