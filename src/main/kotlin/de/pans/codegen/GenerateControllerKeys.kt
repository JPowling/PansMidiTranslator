package de.pans.codegen

import java.io.File
import java.util.*

const val BASE_CLASSPATH = "src/main/kotlin/de/pans/controllers"

fun main() {
    val file = File("src/main/kotlin/de/pans/codegen/Controllers")
    val scanner = Scanner(file)

    var currentDevName = ""

    val devices = mutableMapOf<String, MutableMap<String, Int>>()

    while (scanner.hasNextLine()) {
        val line = scanner.nextLine()

        if (line.endsWith(":")) {
            currentDevName = line.removeSuffix(":")
            devices[currentDevName] = mutableMapOf()
        }
        if (line.contains("=")) {
            val args = line.split("=")
            devices[currentDevName]!![args[0]] = args[1].toInt()
        }
    }

    for (device in devices) {
        val filename = "C" + device.key.replace(" ", "")

        val content = """
        |/* GENERATED FILE; DO NOT EDIT */
        |@file:Suppress("unused", "PropertyName")
        |
        |package de.pans.controllers
        |
        |object ${filename}: Controller("${device.key}") {
        |
        |    ${
            device.value.entries
                .toSortedSet { o1, o2 -> o1.value.compareTo(o2.value) }
                .map { "val ${it.key} = ControllerKey(\"${it.key}\", ${it.value}, ${it.key.startsWith("F")})" }
                .joinToString("\n    ")
        }
        |
        |    override val list: List<ControllerKey> by lazy {
        |        val fields = javaClass.declaredFields.toList()
        |
        |        fields.subList(1, fields.size - 1).map {
        |            it.isAccessible = true
        |            it.get(this) as ControllerKey
        |        }
        |    }
        |}
    """.trimMargin()

        val deviceFile = File("$BASE_CLASSPATH/$filename.kt")

        deviceFile.writeText(content)
    }

    val controllerContent = """
        |package de.pans.controllers

        |open class Controller(val name: String) {
        |
        |    companion object {
        |        val controllers = listOf(${devices.keys.joinToString { "C${it.replace(" ", "")}" }})
        |    }
        |    
        |    open val list = emptyList<ControllerKey>()
        |
        |}
    """.trimMargin()

    File("$BASE_CLASSPATH/Controller.kt").writeText(controllerContent)
}