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

    private var keymap: JSONObject
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

    val freeChannels: List<Int>
        get() = (0..128).filter { !isBoundValue(it) }

    fun isBound(input: Int): Boolean {
        return keymap.has(input.toString())
    }

    fun isBoundValue(output: Int): Boolean {
        return keymap.toMap().values.contains(output.toString())
    }

    fun bind(input: Int, output: Int): Boolean {
        if (!isBound(input)) {
            keymap.put(input.toString(), output.toString())
            save()
            return true
        }
        return false
    }

    fun unbind(input: Int): Boolean {
        if (isBound(input)) {
            keymap.remove(input.toString())
            save()
            return true
        }
        return false
    }

    fun bindNext(input: Int): Int {
        if (isBound(input)) {
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

        bind(input, count)
        return count
    }

    fun getBind(input: Int): Int {
        if (!isBound(input)) {
            return -1
        }
        return keymap.getInt(input.toString())
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

        if (!file.exists()) {
            println("A save called '$filename' was not found!")
            return
        }

        AskForConfirmation(
            "Loading another keymap into the cache will " +
                    "result in loss of current cache, if not saved."
        ) {
            backup()
            val content = file.readText()
            cache.writeText(content)
            keymap = JSONObject(content)
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