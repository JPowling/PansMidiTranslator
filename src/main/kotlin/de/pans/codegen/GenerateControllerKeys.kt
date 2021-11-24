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
        |class ${filename}: Controller("${device.key}") {
        |
        |    ${
            device.value.entries
                .toSortedSet { o1, o2 -> o1.value.compareTo(o2.value) }
                .map { "val ${it.key} = ControllerKey(\"${it.key}\", ${it.value})" }
                .joinToString("\n    ")
        }
        |
        |}
    """.trimMargin()

        val deviceFile = File("$BASE_CLASSPATH/$filename.kt")

        deviceFile.writeText(content)
    }


}