package net.aiscope.gdd_app.ui.util

sealed class DownSamplingRequest(val width: Int, val height: Int)
data class MaximumSizeDownSampling(val maxWidth: Int, val maxHeight: Int) :
    DownSamplingRequest(maxWidth, maxHeight)

data class MinimumSizeDownSampling(val minWidth: Int, val minHeight: Int) :
    DownSamplingRequest(minWidth, minHeight)