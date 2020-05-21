package net.aiscope.gdd_app.extensions

import android.util.Size

private typealias Width = Int

infix fun Width.x(height: Int): Size = Size(this, height)

