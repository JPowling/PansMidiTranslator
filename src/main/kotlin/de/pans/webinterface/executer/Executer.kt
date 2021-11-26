package de.pans.webinterface.executer

abstract class Executer(
    open val execIndex: Int,
    open val pageIndex: Int,
)

class Fader(
    override val execIndex: Int,
    var execFaderType: ExecFaderType = ExecFaderType.Master,
    override val pageIndex: Int,
) : Executer(execIndex, pageIndex) {

    val pos: Double = 0.0
    val value: Double = 0.0
    val button0 = Button(execIndex, 0, pageIndex)
    val button1 = Button(execIndex, 1, pageIndex, ExecButtonType.FLASH)

}


class Button(
    override val execIndex: Int,
    val buttonID: Int,
    pageIndex: Int,
    var execButtonType: ExecButtonType = ExecButtonType.GO,
    var isPressed: Boolean = false,
) : Executer(execIndex, pageIndex) {
    // toggleState: 0 -> off, 1 -> highlighted, 2 -> toggled on


}

enum class ExecFaderType(val type: String) {
    Master("LTP"),
    Crossfade("XF"),
    CrossfadeA(""),
    CrossfadeB(""),
    TempFader("Tmp"),
    MasterSpeed1("Master Speed 1"),
    MasterSpeed2("Master Speed 2"),
    MasterSpeed3("Master Speed 3"),
    MasterSpeed4("Master Speed 4"),
    MasterRate(""),
    ProgTime(""),
    ExecTime(""),
    Leer(""),
    UNDIFINED("-1"),
    ;

    companion object {
        fun getByType(type: String): ExecFaderType {
            return values().first { it.type == type }
        }
    }
}

enum class ExecButtonType(val type: String?) {
    GO("Go"),
    GO_BACK("GoBack"),
    PAUSE("Pause"),
    TOGGLE("Toggle"),
    TEMP("Temp"),
    LEARN("Learn"),
    FLASH("Flash"),
    SELECT("Select"),
    SWOP("Swop"),
    EMPTY(null),
    UNDIFINED("-1"),
    ;

    companion object {
        fun getByType(type: String): ExecButtonType {
            return values().first { it.type == type }
        }
    }

    val isRed: Boolean by lazy {
        this in listOf(TOGGLE, PAUSE)
    }

    val isGreen: Boolean by lazy {
        this in listOf(GO, GO_BACK, LEARN, SELECT)
    }

    val isYellow: Boolean by lazy {
        !isRed && !isGreen
    }
}