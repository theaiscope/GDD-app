package net.aiscope.gdd_app.ui.metadata

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.InputStream

@RunWith(AndroidJUnit4::class)
class SampleImagesAdapterTest {
    private val testContext = InstrumentationRegistry.getInstrumentation().context
    private val targetContext = InstrumentationRegistry.getInstrumentation().targetContext
    private val cacheDir = targetContext.cacheDir
    private lateinit var targetFile: File

    @Before
    fun setup() {
        targetFile = File(targetContext.filesDir, "test.bmp");
        targetFile.createNewFile()

        val stream = testContext.assets.open("w3c_home.bmp");
        stream.toFile(targetFile)
    }


    @Test
    fun shouldWriteToCache() {
        val b = runBlocking {
            decodeSampledBitmapAndCache(targetFile, 20, 20, cacheDir)
        }

        assertTrue(cacheDir.isDirectory)
        val cachedFiles = cacheDir.listFiles()

        assertTrue(cachedFiles.any { file -> file.name.equals("test_20x20.bmp", true) })
        assertTrue(b.height < 50)
        assertTrue(b.width < 50)
    }

    @Test
    fun shouldNotWriteToCacheTwice() {
        runBlocking {
            decodeSampledBitmapAndCache(targetFile, 20, 20, cacheDir)
        }

        assertTrue(cacheDir.isDirectory)
        val cacheFile = File(cacheDir, "test_20x20.bmp")
        assertTrue(cacheFile.exists())
        val timestamp = cacheFile.lastModified()

        //Run for the second time. this should not write to cache again.
        runBlocking {
            decodeSampledBitmapAndCache(targetFile, 20, 20, cacheDir)
        }

        assertEquals(timestamp, cacheFile.lastModified())
    }

    private fun InputStream.toFile(file: File) {
        file.outputStream().use { this.copyTo(it) }
    }
}