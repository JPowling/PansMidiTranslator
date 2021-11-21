package de.pans.webinterface.executer

abstract class Executer(open val ID: Int) {

}


class Fader(override val ID: Int,var faderType: FaderType = FaderType.Master) : Executer(ID) {
    val pos: Float = 0f
    val value: Double = 0.0
    val button1 = Button(ID)
    val button2 = Button(ID, ExecType.Flash)

}


class Button(override val ID: Int, var execType: ExecType = ExecType.GO, var toggleState: Int = 0) : Executer(ID) {
    // toggleState: 0 -> off, 1 -> highlighted, 2 -> toggled on
}

enum class FaderType {
    Master,
    Crossfade,
    CrossfadeA,
    CrossfadeB,
    TempFader,
    MasterSpeed1,
    MasterSpeed2,
    MasterSpeed3,
    MasterSpeed4,
    MasterRate,
    ProgTime,
    ExecTime,
    Leer
}

enum class ExecType {
    GO,
    GoBack,
    Pause,
    Toggle,
    Temp,
    Learn,
    Flash,
    Select,
    Swop
}