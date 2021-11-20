package de.pans.controllers

@Suppress("unused")
enum class NanoKontrol2(val id: Int) {

    PLAY(41),
    STOP(42),
    BACKWARD(43),
    FORWARD(44),
    RECORD(45),
    CYCLE(46),
    TRACK_LEFT(58),
    TRACK_RIGHT(59),
    MARKER_SET(60),
    MARKER_LEFT(61),
    MARKER_RIGHT(62),

    COL1_R(64),
    COL1_M(48),
    COL1_S(32),
    COL1_FADER(0),
    COL1_ROTATOR(16),

    COL2_R(65),
    COL2_M(49),
    COL2_S(33),
    COL2_FADER(1),
    COL2_ROTATOR(17),

    COL3_R(66),
    COL3_M(50),
    COL3_S(34),
    COL3_FADER(2),
    COL3_ROTATOR(18),

    COL4_R(67),
    COL4_M(51),
    COL4_S(35),
    COL4_FADER(3),
    COL4_ROTATOR(19),

    COL5_R(68),
    COL5_M(52),
    COL5_S(36),
    COL5_FADER(4),
    COL5_ROTATOR(20),

    COL6_R(69),
    COL6_M(53),
    COL6_S(37),
    COL6_FADER(5),
    COL6_ROTATOR(21),

    COL7_R(70),
    COL7_M(54),
    COL7_S(38),
    COL7_FADER(6),
    COL7_ROTATOR(22),

    COL8_R(71),
    COL8_M(55),
    COL8_S(39),
    COL8_FADER(7),
    COL8_ROTATOR(23),
    NONE(-1);

    companion object {
        fun getByID(id: Int): NanoKontrol2 {
            for (nano in values()) {
                if (nano.id == id) {
                    return nano
                }
            }
            return NONE
        }
    }
}