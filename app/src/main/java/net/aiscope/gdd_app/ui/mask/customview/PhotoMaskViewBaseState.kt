package net.aiscope.gdd_app.ui.mask.customview

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PhotoMaskViewBaseState(
    val pathsPaintsAndStagesNames: List<PathAndPaint>,
    val undoPendingPaths: Int,
    val currentBrushColor: Int
) : Parcelable
