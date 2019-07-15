package net.aiscope.gdd_app.ui.mask

import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.graphics.BitmapFactory
import kotlinx.android.synthetic.main.activity_mask.*
import net.aiscope.gdd_app.R


class MaskActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var imagePath = "/data/user/0/net.aiscope.gdd_app/files/faf868b2-4bf8-47bc-8d9f-0497e762ae2a.jpg"
        imagePath = intent.getStringExtra("imagePath") ?: imagePath

        setContentView(R.layout.activity_mask)

        val bmp = readImage(imagePath)
        maskView.originalBitmap = bmp
    }

    private fun readImage(filepath: String): Bitmap {
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        return BitmapFactory.decodeFile(filepath, options)
    }
}