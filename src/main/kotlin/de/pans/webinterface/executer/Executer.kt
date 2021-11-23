package de.pans.webinterface.executer

abstract class Executer(
    open val execIndex: Int,
    open val pageIndex: Int,
){

}

class Fader(
    override val execIndex: Int,
    var faderType: FaderType = FaderType.Master,
    override val pageIndex: Int,
) : Executer(execIndex, pageIndex) {

    val pos: Double = 0.0
    val value: Double = 0.0
    val button0 = Button(execIndex, 0, pageIndex)
    val button1 = Button(execIndex, 1, pageIndex, ExecType.Flash)

}


class Button(
    override val execIndex: Int,
    val buttonID: Int,
    pageIndex: Int,
    var execType: ExecType = ExecType.GO,
    var isPressed: Boolean = false
) : Executer(execIndex, pageIndex) {
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