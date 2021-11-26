package de.pans.dot2

import de.pans.midiio.MidiKey
import de.pans.webinterface.WebIO
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

        val str = json.getJSONObject(key.deviceName).get(key.channel.toString())
        return SendMethod.fromString(str.toString())
    }

    fun getAllExecutors(): List<Pair<MidiKey, EXEC>> {
        val list = mutableListOf<Pair<MidiKey, EXEC>>()
        for (device in json.keySet()) {
            val devJson = json.getJSONObject(device)

            for (keyID in devJson.keySet()) {
                val key = MidiKey(device, keyID.toInt())
                val sendMethod = SendMethod.fromString(devJson.get(keyID).toString())

                if (sendMethod is EXEC) {
                    list.add(key to sendMethod)
                }
            }
        }

        return list
    }

    fun checkThatDeviceExists(key: MidiKey) {
        if (!json.has(key.deviceName)) {
            json.put(key.deviceName, JSONObject())
        }
    }

    fun save() = Settings.save()

    data class CMD(val name: String) : SendMethod()
    data class EXEC(val execID: Int, var value: Double = 0.0) : SendMethod() {
        val readTypeButton by lazy { WebIO.readButtonType(execID, 0, 0) }
        val readStateButton by lazy { WebIO.readButtonState(execID, 0, 0) }

        val isFader = execID in 0..50

        override fun toString(): String {
            return "EXEC(execID=$execID)"
        }
    }

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