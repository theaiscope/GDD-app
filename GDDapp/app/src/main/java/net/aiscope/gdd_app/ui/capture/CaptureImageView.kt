package net.aiscope.gdd_app.ui.capture

import android.graphics.Bitmap

interface CaptureImageView {

    fun setPreviewImage(bm: Bitmap?)

    fun takePhoto()

    var onCameraError: (String?) -> Unit

}