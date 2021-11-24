package de.pans.dot2

import de.pans.main.AskForConfirmation
import de.pans.midiio.MidiKey
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.nio.file.Files
import java.nio.file.LinkOption
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Settings {

    private const val KEYMAP_CACHE = "./settings_cache"
    private val SAVE_DIR = File("./settings/")
    private val BACKUP_DIR = File("./.settings_backups/")

    private var cache = File(KEYMAP_CACHE)

    private lateinit var settings: JSONObject
    private val midiKeymap: JSONObject
        get() {
            return settings.run {
                if (!has("midikeymap"))
                    put("midikeymap", JSONObject())
                getJSONObject("midikeymap")
            }
        }

    private val toJson: String
        get() = settings.toString()

    init {
        if (!SAVE_DIR.exists()) {
            SAVE_DIR.mkdir()
        }
        if (!BACKUP_DIR.exists()) {
            BACKUP_DIR.mkdir()

            try {
                Files.setAttribute(BACKUP_DIR.toPath().toAbsolutePath(), "dos:hidden", true, LinkOption.NOFOLLOW_LINKS)
            } catch (e: Exception) {
                // I don't know if this throws an exception when using linux but i'll catch it anyways
            }
        }

        if (!cache.exists()) {
            cache.createNewFile()
            default()
        }
        reloadCache()
    }

    val freeChannels: List<Int>
        get() = (0..128).filter { !isBoundValue(it) }

    fun isValidMIDIChannel(toCheck: Int) = toCheck in 0..128

    fun isBound(midiKey: MidiKey): Boolean {
        if (midiKeymap.has(midiKey.deviceName)) {
            return midiKeymap
                .getJSONObject(midiKey.deviceName)
                .has(midiKey.channel.toString())
        }
        return false
    }

    fun isBoundValue(output: Int): Boolean {
        var assigned = false
        for (midiDev in midiKeymap.keySet()) {
            if (midiKeymap.getJSONObject(midiDev).toMap().values.contains(output.toString())) {
                assigned = true
                break
            }
        }
        return assigned
    }

    fun bindMIDI(midiKey: MidiKey, output: Int, overwrite: Boolean = false): Boolean {
        if (isBoundValue(output) && overwrite) {
            getWhatsBoundTo(output)?.let { unbind(it) }
        }
        if (!isBound(midiKey)) {
            if (!midiKeymap.has(midiKey.deviceName)) {
                midiKeymap.put(midiKey.deviceName, JSONObject())
            }

            midiKeymap
                .getJSONObject(midiKey.deviceName)
                .put(midiKey.channel.toString(), output.toString())
            save()
            return true
        }
        return false
    }

    fun unbind(midiKey: MidiKey): Boolean {
        if (isBound(midiKey)) {
            midiKeymap.getJSONObject(midiKey.deviceName).remove(midiKey.channel.toString())
            save()
            return true
        }
        return false
    }

    fun bindNext(midiKey: MidiKey): Int {
        if (isBound(midiKey)) {
            return -1
        }

        var count = -1

        for (i in 0..127) {
            if (!isBoundValue(i)) {
                count = i
                break
            }
        }
        if (count == -1) {
            return -2
        }

        bindMIDI(midiKey, count)
        return count
    }

    fun getBind(midiKey: MidiKey): Int {
        if (!isBound(midiKey)) {
            return -1
        }
        return midiKeymap.getJSONObject(midiKey.deviceName).getString(midiKey.channel.toString()).toInt()
    }

    fun getWhatsBoundTo(output: Int): MidiKey? {
        if (!isBoundValue(output)) {
            return null
        }

        var midiKey: MidiKey? = null

        for (midiDev in midiKeymap.keySet()) {
            val midiDevJson = midiKeymap.getJSONObject(midiDev)

            val channel = midiDevJson.toMap()
                .filter { it.value.toString().toInt() == output }
                .map { it.key.toInt() }.firstOrNull()

            if (channel != null) {
                midiKey = MidiKey(midiDev, channel)
            }
        }

        return midiKey
    }

    fun put(key: String, value: Any) {
        settings.put(key, value)
        save()
    }

    fun append(key: String, value: Any) {
        settings.append(key, value)
        save()
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getList(key: String): List<T> {
        if (!settings.has(key)) {
            settings.put(key, JSONArray())
        }

        return settings.getJSONArray(key).toList() as List<T>
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> get(key: String): T {
        return settings.get(key) as T
    }

    fun has(key: String): Boolean {
        return settings.has(key)
    }

    fun reset() {
        backup()
        settings.clear()
        put("ip", "127.0.0.1")
        save()
        reloadCache()
    }

    private fun save() {
        cache.writeText(toJson)
    }

    fun saveAs(filename: String) {
        val file = File("./keymaps/$filename.txt")

        if (file.exists()) {
            AskForConfirmation(
                "A file named \"$filename.txt\" already exists. " +
                        "Proceeding will result in loss of data."
            ) {
                backup(file)
                file.writeText(toJson)
            }
            return
        }
        file.writeText(toJson)
    }

    fun loadFrom(filename: String) {
        val file = File("./keymaps/$filename.txt")

        if (!file.exists()) {
            println("A save called '$filename' was not found!")
            return
        }

        AskForConfirmation(
            "Loading another keymap into the cache will " +
                    "result in loss of current cache, if not saved."
        ) {
            backup()
            cache.writeText(file.readText())
        }
    }

    private fun reloadCache() {
        settings = JSONObject(cache.readLines().first())
    }

    private fun backup(file: File = cache) {
        if (file.readText() == "{}")
            return

        val time = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss_SSS").format(LocalDateTime.now())
        val backup = File("${BACKUP_DIR.path}/$time")

        file.copyTo(backup)
    }

    private fun default() {
        put("ip", "127.0.0.1")
    }

}