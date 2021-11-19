package de.pans.dot2

import de.pans.main.AskForConfirmation
import org.json.JSONObject
import java.io.File
import java.nio.file.Files
import java.nio.file.LinkOption
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

object MappingSettings {

    private const val KEYMAP_CACHE = "./keymap_cache"
    private val SAVE_DIR = File("./keymaps/")
    private val BACKUP_DIR = File("./.keymap_backups/")

    private val keymap: JSONObject
    private var cache = File(KEYMAP_CACHE)

    private val toJson: String
        get() {
            return keymap.toString()
        }

    init {
        if (!SAVE_DIR.exists()) {
            SAVE_DIR.mkdir()
        }
        if (!BACKUP_DIR.exists()) {
            BACKUP_DIR.mkdir()
            Files.setAttribute(BACKUP_DIR.toPath().toAbsolutePath(), "dos:hidden", true, LinkOption.NOFOLLOW_LINKS)
        }
        if (!cache.exists()) {
            cache.createNewFile()
        }
        val scanner = Scanner(cache)

        keymap = if (scanner.hasNextLine()) {
            JSONObject(scanner.nextLine())
        } else {
            JSONObject()
        }
    }

    fun isBound(input: Byte): Boolean {
        return keymap.has(input.toString())
    }

    fun isBoundValue(output: Byte): Boolean {
        return keymap.toMap().values.contains(output.toString())
    }

    fun bind(input: Byte, output: Byte): Boolean {
        if (!isBound(input)) {
            keymap.put(input.toString(), output.toString())
            save()
            return true
        }
        return false
    }

    fun unbind(input: Byte): Boolean {
        if (isBound(input)) {
            keymap.remove(input.toString())
            save()
            return true
        }
        return false
    }

    fun bindNext(input: Byte): Int {
        if (isBound(input)) {
            return 0
        }

        var count = -1

        for (i in 0..127) {
            if (!isBoundValue(i.toByte())) {
                println(i)
                count = i
                break
            }
        }
        if (count == -1) {
            return 2
        }

        bind(input, count.toByte())

        return 1
    }

    fun getBind(input: Byte): Byte {
        return keymap.getInt(input.toString()).toByte()
    }

    fun unbindAll() {
        backup()
        keymap.clear()
        save()
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

        AskForConfirmation(
            "Loading another keymap into the cache will " +
                    "result in loss of current cache, if not saved."
        ) {
            backup()
            cache.writeText(file.readText())
        }
    }

    private fun backup(file: File = cache) {
        if (file.readText() == "{}")
            return

        val time = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss_SSS").format(LocalDateTime.now())
        val backup = File("${BACKUP_DIR.path}/$time")

        file.copyTo(backup)
    }

}