package de.pans.command

import de.pans.MD5
import de.pans.dot2.Settings

object CommandWeb : Command("web", "websetup") {
    override fun handle(args: List<String>) {
        if (args.isEmpty()) {
            showHelp()
        }

        when (args[0]) {
            "passwd" -> {
                if (args.size < 2) {
                    if (Settings.has("passwd")) {
                        println("Current DOT2 passsword: ${Settings.get<String>("passwd")}")
                        println("Haha, now you only know the hash of the password :P. But i don't know it either...")
                        return
                    }
                    println("You haven't specified a password yet!")
                    return
                }

                val passwd = args.subList(1, args.size).joinToString(" ")
                val hash = MD5.hash(passwd)

                Settings.put("passwd", hash)
                println("Successfully changed password to $hash.")
            }
            "ip" -> {
                if (args.size < 2) {
                    println("Current DOT2 IP address: ${Settings.get<String>("ip")}")
                    return
                }

                val ip = args[1]
                Settings.put("ip", ip)
                println("Successfully changed the IP address to $ip")
            }
        }
    }

    override fun showHelp() {
        println(
            """Usage of 'web':
            |web passwd [<password>]: Set or read DOT2 web password
            |web ip [<ip>]: Set or read DOT2 web ip (default: 127.0.0.1)
            |web connect: Establish connection to DOT2 web and change mode from MIDI to WEB
        """.trimMargin()
        )
    }
}