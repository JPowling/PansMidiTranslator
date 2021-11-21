package de.pans

import java.security.MessageDigest
import javax.xml.bind.DatatypeConverter

object MD5 {

    private val md5: MessageDigest = MessageDigest.getInstance("MD5")

    fun hash(input: String): String {
        val hashBytes = md5.digest(input.toByteArray())
        return DatatypeConverter.printHexBinary(hashBytes).lowercase()
    }

}