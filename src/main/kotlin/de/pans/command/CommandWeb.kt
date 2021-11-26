package de.pans.command

import de.pans.MD5
import de.pans.dot2.Settings
import de.pans.main.Mode
import de.pans.main.Translator
import de.pans.main.printerr
import de.pans.webinterface.ServerResponse
import de.pans.webinterface.WebIO

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
            "connect", "c" -> {
                if (Translator.mode == Mode.WEB) {
                    println("You are already in WEB mode!")
                    return
                }

                if (!Settings.has("passwd")) {
                    println("You have to specify a password first! Use 'web passwd <password>'")
                    return
                }

                val host = Settings.get<String>("ip")
                val passwd = Settings.get<String>("passwd")

                when (WebIO.connect(host, pwdHash = passwd)) {
                    ServerResponse.OK -> {
                        println("Successfully connected to dot2. Now changing to WEB mode.")
                        println("You might want to setup the keymap using 'webmap'.")
                        Translator.mode = Mode.WEB
                    }
                    ServerResponse.Unauthorized -> {
                        printerr("Couldn't connect as the password is wrong!")
                    }
                    else -> {
                        printerr(
                            "An internal error occured. " +
                                    "I won't tell you why as I don't know and I actually don't care."
                        )
                    }
                }
            }
            "disconnect", "dc" -> {
                if (Translator.mode == Mode.MIDI) {
                    println("You are not connected to dot2!")
                    return
                }
                Translator.mode = Mode.MIDI
                WebIO.disconnect()
                println("Changed mode back to MIDI.")
            }
            "reconnect", "rc" -> {
                if (Translator.mode != Mode.WEB) {
                    println("You are not connected to dot2!")
                    return
                }

                handle(listOf("dc"))
                handle(listOf("c"))
            }
            "autoconnect", "ac" -> {
                if (Settings.get("autoconnect")) {
                    println("Turning off autoconnect!")
                    Settings.put("autoconnect", false)
                } else {
                    println("Turning on autoconnect!")
                    Settings.put("autoconnect", true)
                }
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