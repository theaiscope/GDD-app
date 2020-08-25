package net.aiscope.gdd_app.ui.util

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.InputStream

@RunWith(AndroidJUnit4::class)
class BitmapReaderTest {
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
    fun shouldReduceToRequestedSize() {
        val b = runBlocking {
                BitmapReader.decodeSampledBitmapFromResource(targetFile, 20, 20, true)
        }

        //So the determining the sample size happens in a way that leaves both height and width BIGGER than the
        //requested size...
        assertTrue(b.height < 50)
        assertTrue(b.width < 50)
        assertTrue(b.height > 20)
        assertTrue(b.width > 20)
    }

    @Test
    fun shouldReduceToMaximumSize() {
        val b = runBlocking {
            BitmapReader.decodeSampledBitmapToMaximum(targetFile, 50, 50, true)
        }

        //So here we want the dimensions to be smaller than the requested size
        assertTrue(b.height < 50)
        assertTrue(b.width < 50)
    }

    private fun InputStream.toFile(file: File) {
        file.outputStream().use { this.copyTo(it) }
    }
}