package net.aiscope.gdd_app.ui.util

import android.graphics.Bitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import net.aiscope.gdd_app.test.extensions.getAssetStream
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThat
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.InputStream
import org.hamcrest.CoreMatchers.`is` as isEqualTo

@RunWith(AndroidJUnit4::class)
class BitmapReaderTest {
    private val testContext = InstrumentationRegistry.getInstrumentation().context
    private val targetContext = InstrumentationRegistry.getInstrumentation().targetContext
    private val cacheDir = targetContext.cacheDir
    private lateinit var targetFile: File

    @Before
    fun setup() {
        // we are using JPG to capture images
        targetFile = File(targetContext.filesDir, "test.jpg")
        targetFile.createNewFile()

        testContext.getAssetStream("photo.jpg").toFile(targetFile)

        //Clear cache dir
        targetContext.cacheDir.listFiles().forEach { file -> file.deleteRecursively() }
    }


    @Test
    fun shouldReduceToRequestedSize() {
        val b = runBlocking {
                BitmapReader.decodeSampledBitmapFromResource(targetFile, MinimumSizeDownSampling(20, 20), mutable = false)
        }

        //Determining the sample size happens in a way that leaves both height and width BIGGER than the
        //requested size...
        assertTrue("Dimensions bigger than expected", b.height < 50)
        assertTrue("Dimensions bigger than expected", b.width < 50)
        assertTrue("Dimensions smaller than expected", b.height >= 20)
        assertTrue("Dimensions smaller than expected", b.width >= 20)
        assertFalse("Expected to be immutable", b.isMutable)
        assertThat("Bitmap size corresponds to RGB565 encoding", b.allocationByteCount, isEqualTo(b.byteCountForRgb565()))
    }

    @Test
    fun shouldReduceToMaximumSize() {
        val b = runBlocking {
            BitmapReader.decodeSampledBitmapFromResource(targetFile, MaximumSizeDownSampling(50, 50), mutable = true)
        }

        //Here we want the dimensions to be smaller than the requested size
        assertTrue("Dimensions bigger than expected", b.height <= 50)
        assertTrue("Dimensions bigger than expected", b.width <= 50)
        assertTrue("Expected to be mutable", b.isMutable)
        assertThat("Bitmap size corresponds to RGB565 encoding", b.allocationByteCount, isEqualTo(b.byteCountForRgb565()))
    }

    @Test
    fun shouldWriteToCache() {
        val b = runBlocking {
            BitmapReader.decodeSampledBitmapAndCache(targetFile, 20, 20, cacheDir)
        }

        assertTrue(cacheDir.isDirectory)
        val cachedFiles = cacheDir.listFiles()

        assertTrue("Cache file not found", cachedFiles.any { file -> file.name.equals("test_20x20.jpg", true) })
        assertTrue("Dimensions bigger than expected",  b.height < 50)
        assertTrue("Dimensions bigger than expected", b.width < 50)
        assertTrue("Dimensions smaller than expected", b.height >= 20)
        assertTrue("Dimensions smaller than expected", b.width >= 20)
    }

    @Test
    fun shouldNotWriteToCacheTwice() {
        runBlocking {
            BitmapReader.decodeSampledBitmapAndCache(targetFile, 20, 20, cacheDir)
        }

        assertTrue(cacheDir.isDirectory)
        val cacheFile = File(cacheDir, "test_20x20.jpg")
        assertTrue("Cache file not found", cacheFile.exists())
        val timestamp = cacheFile.lastModified()

        // Since the lastModified is in seconds, make sure we would get a new value
        // if the write occurs.
        Thread.sleep(1200)

        //Run for the second time. this should not write to cache again.
        runBlocking {
            BitmapReader.decodeSampledBitmapAndCache(targetFile, 20, 20, cacheDir)
        }

        assertEquals("Cache file should not be modified", timestamp, cacheFile.lastModified())
    }

    private fun InputStream.toFile(file: File) {
        file.outputStream().use { this.copyTo(it) }
    }

    private fun Bitmap.byteCountForRgb565() : Int {
        // each pixel is represented by 2 bytes
        return this.width * this.height * 2
    }
}