package de.pans.dot2

import de.pans.midiio.MidiKey
import org.json.JSONObject

object WebSettings {

    private val json: JSONObject
        get() {
            return Settings.settings.run {
                if (!has("webkeymap"))
                    put("webkeymap", JSONObject())
                getJSONObject("webkeymap")
            }
        }

    fun bind(key: MidiKey, value: Any) {
        checkThatDeviceExists(key)

        val devjson = json.getJSONObject(key.deviceName)
        devjson.put(key.channel.toString(), value)
        save()
    }

    fun unbind(key: MidiKey) {
        checkThatDeviceExists(key)

        json.getJSONObject(key.deviceName).remove(key.channel.toString())
        save()
    }

    fun isBound(key: MidiKey): Boolean {
        checkThatDeviceExists(key)

        return json.getJSONObject(key.deviceName).has(key.channel.toString())
    }

    fun getBind(key: MidiKey): SendMethod? {
        checkThatDeviceExists(key)
        if (!isBound(key)) {
            return null
        }

        val str = json.getJSONObject(key.deviceName).getString(key.channel.toString())
        return SendMethod.fromString(str)
    }

    fun checkThatDeviceExists(key: MidiKey) {
        if (!json.has(key.deviceName)) {
            json.put(key.deviceName, JSONObject())
        }
    }

    fun save() = Settings.save()

    data class CMD(val name: String) : SendMethod()
    data class EXEC(val execID: Int, var value: Double = 0.0) : SendMethod()

    abstract class SendMethod {
        companion object {
            fun fromString(str: String): SendMethod {
                val data = str
                    .replaceFirst("CMD(", "")
                    .substringBefore(")")
                    .split(", ")
                    .map { it.split("=")[1] }

                if (str.startsWith("CMD")) {
                    return CMD(data[0])
                }
                if (str.startsWith("EXEC")) {
                    return EXEC(data[0].toInt())
                }
                TODO()
            }
        }
    }

}