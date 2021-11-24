package de.pans.command

import de.pans.dot2.Settings
import de.pans.main.Translator
import de.pans.midiio.MidiDevice
import org.json.JSONArray
import javax.sound.midi.MidiSystem
import javax.sound.midi.MidiUnavailableException

object CommandDevice : Command("device", "dev", "devices") {
    override fun handle(args: List<String>) {
        if (args.isEmpty()) {
            showHelp()
            return
        }

        when (args[0]) {
            "add", "load" -> {
                if (args.size < 2) {
                    println("You need to specify the name of the MIDI device!")
                }
                val deviceName = args.subList(1, args.size).joinToString(" ")

                val device: MidiDevice
                try {
                    device = MidiDevice(deviceName)

                    if (!Translator.load(device)) {
                        println("MIDI device ${device.name} is already loaded!")
                        device.unload()
                        return
                    }
                } catch (e: MidiUnavailableException) {
                    println("Can't find/load '$deviceName!'")
                    return
                }

                Settings.append("midiDevs", device.name)
                println("Successfully loaded ${device.name}!")
            }
            "remove", "unload" -> {
                if (args.size < 2) {
                    println("You need to specify the name of the MIDI device!")
                }
                val deviceName = args.subList(1, args.size).joinToString(" ")

                val device = Translator.midiDevs.firstOrNull { it.name.lowercase().contains(deviceName) }
                if (device == null) {
                    println("MIDI device '$deviceName' isn't loaded!")
                    return
                }

                Translator.unload(device)

                val array = Settings.get<JSONArray>("midiDevs").filter { it != device.name }
                Settings.put("midiDevs", JSONArray(array))
                println("Successfully unloaded ${device.name}!")
            }
            "reload" -> {
                for (it in Translator.midiDevs) {
                    it.reload()
                }
            }
            "listall", "list" -> {
                println("Available MIDI devices:")
                println(MidiSystem.getMidiDeviceInfo()
                    .distinctBy { it.name }
                    .filterNot {
                        it.name == "MIDI Mapper"
                                || it.name == "Real Time Sequencer"
                                || it.name == "Gervill"
                                || it.name.startsWith("Microsoft")
                                || it.name.startsWith("loopMIDI Port")
                    }.toSortedSet { o1, o2 -> o1.name.compareTo(o2.name) }
                    .joinToString { it.name })
            }
            "listloaded", "loaded" -> {
                println("Loaded MIDI devices:")
                if (Translator.midiDevs.isEmpty()) {
                    println("None")
                } else {
                    println(Translator.midiDevs
                        .toSortedSet { o1, o2 -> o1.name.compareTo(o2.name) }
                        .joinToString { it.name })
                }
            }
        }

    }

    override fun showHelp() {
        println(
            """Usage of 'device':
            |dev load <name>: Loads specified MIDI device.
            |dev unload <name> Unloads specified MIDI device. (NOTE: bound keys won't be unbound!)
            |dev reload: Reloads every loaded MIDI device. (when you accidentally unplugged a MIDI device, run this)
            |dev list: Lists every available MIDI devices.
            |dev loaded: Lists every loaded MIDI devices.
        """.trimMargin()
        )
    }
}