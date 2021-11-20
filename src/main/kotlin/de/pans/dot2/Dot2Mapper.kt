package de.pans.dot2

object Dot2Mapper {

    fun map(bytes: List<Int>): List<Int> {
        val bytes = bytes.toMutableList()
        if (MappingSettings.isBound(bytes[1])) {
            bytes[0] = 144
            bytes[1] = MappingSettings.getBind(bytes[1])

            return bytes
        }
        return emptyList()
    }

}