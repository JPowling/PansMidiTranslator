/* GENERATED FILE; DO NOT EDIT */
@file:Suppress("unused", "PropertyName")

package de.pans.controllers

object CnanoKONTROL2 : Controller("nanoKONTROL2") {

    val F_COL1_FADER = ControllerKey("F_COL1_FADER", 0, true)
    val F_COL2_FADER = ControllerKey("F_COL2_FADER", 1, true)
    val F_COL3_FADER = ControllerKey("F_COL3_FADER", 2, true)
    val F_COL4_FADER = ControllerKey("F_COL4_FADER", 3, true)
    val F_COL5_FADER = ControllerKey("F_COL5_FADER", 4, true)
    val F_COL6_FADER = ControllerKey("F_COL6_FADER", 5, true)
    val F_COL7_FADER = ControllerKey("F_COL7_FADER", 6, true)
    val F_COL8_FADER = ControllerKey("F_COL8_FADER", 7, true)
    val F_COL1_ROTATOR = ControllerKey("F_COL1_ROTATOR", 16, true)
    val F_COL2_ROTATOR = ControllerKey("F_COL2_ROTATOR", 17, true)
    val F_COL3_ROTATOR = ControllerKey("F_COL3_ROTATOR", 18, true)
    val F_COL4_ROTATOR = ControllerKey("F_COL4_ROTATOR", 19, true)
    val F_COL5_ROTATOR = ControllerKey("F_COL5_ROTATOR", 20, true)
    val F_COL6_ROTATOR = ControllerKey("F_COL6_ROTATOR", 21, true)
    val F_COL7_ROTATOR = ControllerKey("F_COL7_ROTATOR", 22, true)
    val F_COL8_ROTATOR = ControllerKey("F_COL8_ROTATOR", 23, true)
    val B_COL1_S = ControllerKey("B_COL1_S", 32, false)
    val B_COL2_S = ControllerKey("B_COL2_S", 33, false)
    val B_COL3_S = ControllerKey("B_COL3_S", 34, false)
    val B_COL4_S = ControllerKey("B_COL4_S", 35, false)
    val B_COL5_S = ControllerKey("B_COL5_S", 36, false)
    val B_COL6_S = ControllerKey("B_COL6_S", 37, false)
    val B_COL7_S = ControllerKey("B_COL7_S", 38, false)
    val B_COL8_S = ControllerKey("B_COL8_S", 39, false)
    val B_PLAY = ControllerKey("B_PLAY", 41, false)
    val B_STOP = ControllerKey("B_STOP", 42, false)
    val B_REWIND = ControllerKey("B_REWIND", 43, false)
    val B_FORWARD = ControllerKey("B_FORWARD", 44, false)
    val B_REC = ControllerKey("B_REC", 45, false)
    val B_CYCLE = ControllerKey("B_CYCLE", 46, false)
    val B_COL1_M = ControllerKey("B_COL1_M", 48, false)
    val B_COL2_M = ControllerKey("B_COL2_M", 49, false)
    val B_COL3_M = ControllerKey("B_COL3_M", 50, false)
    val B_COL4_M = ControllerKey("B_COL4_M", 51, false)
    val B_COL5_M = ControllerKey("B_COL5_M", 52, false)
    val B_COL6_M = ControllerKey("B_COL6_M", 53, false)
    val B_COL7_M = ControllerKey("B_COL7_M", 54, false)
    val B_COL8_M = ControllerKey("B_COL8_M", 55, false)
    val B_TRACK_L = ControllerKey("B_TRACK_L", 58, false)
    val B_TRACK_R = ControllerKey("B_TRACK_R", 59, false)
    val B_SET = ControllerKey("B_SET", 60, false)
    val B_MARKER_L = ControllerKey("B_MARKER_L", 61, false)
    val B_MARKER_R = ControllerKey("B_MARKER_R", 62, false)
    val B_COL1_R = ControllerKey("B_COL1_R", 64, false)
    val B_COL2_R = ControllerKey("B_COL2_R", 65, false)
    val B_COL3_R = ControllerKey("B_COL3_R", 66, false)
    val B_COL4_R = ControllerKey("B_COL4_R", 67, false)
    val B_COL5_R = ControllerKey("B_COL5_R", 68, false)
    val B_COL6_R = ControllerKey("B_COL6_R", 69, false)
    val B_COL7_R = ControllerKey("B_COL7_R", 70, false)
    val B_COL8_R = ControllerKey("B_COL8_R", 71, false)

    override val list: List<ControllerKey> by lazy {
        val fields = javaClass.declaredFields.toList()

        fields.subList(1, fields.size - 1).map {
            it.isAccessible = true
            it.get(this) as ControllerKey
        }
    }
}