package net.aiscope.gdd_app.ui.main

import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener

object NoOpPermissionListener : PermissionListener {
    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
        // do nothing
    }

    override fun onPermissionRationaleShouldBeShown(
        permission: PermissionRequest?,
        token: PermissionToken?
    ) {
        // do nothing
    }

    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
        // do nothing
    }
}
