package net.aiscope.gdd_app.test.extensions

import android.content.Context
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream

fun Context.getAssetStream(fileName: String): InputStream {
    return try {
        this.resources.assets.open(fileName)
    } catch (ex: FileNotFoundException) {
        this.javaClass.classLoader!!.getResourceAsStream("assets" + File.separator + fileName)
    }
}