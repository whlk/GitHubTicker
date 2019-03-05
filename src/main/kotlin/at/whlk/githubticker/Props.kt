package at.whlk.githubticker

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*

class Props {

    private val properties: Properties by lazy {
        val props = Properties()
        val file = File(PROPERTIES_FILE)
        if (!file.exists()) {
            file.createNewFile()
        }
        FileInputStream(file).use {
            props.load(it)
        }
        return@lazy props
    }

    operator fun get(key: String): String? {
        return properties.getProperty(key)
    }

    fun set(key: String, value: String) {
        properties.setProperty(key, value)
        FileOutputStream(PROPERTIES_FILE).use {
            properties.store(it, "githubticker properties file")
        }
    }

    companion object {
        private const val PROPERTIES_FILE = "githubticker.properties"
    }
}