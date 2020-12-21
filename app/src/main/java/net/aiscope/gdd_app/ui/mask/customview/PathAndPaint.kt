package net.aiscope.gdd_app.ui.mask.customview

import android.graphics.Paint
import android.os.Parcel
import android.os.Parcelable
import androidx.core.os.ParcelCompat
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.TypeParceler

@Parcelize
@TypeParceler<Paint, PaintParceler>
data class PathAndPaint(
    val path: PointToPointPath,
    val paint: Paint
) : Parcelable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PathAndPaint

        if (path != other.path) return false

        return paint.similarTo(other.paint)
    }

    override fun hashCode(): Int {
        var result = path.hashCode()
        result = 31 * result + paint.similarHashCode()
        return result
    }
}

private object PaintParceler : Parceler<Paint> {
    override fun create(parcel: Parcel): Paint = with(parcel) {
        val color = readInt()
        val strokeWidth = readFloat()
        val isErase = ParcelCompat.readBoolean(parcel)
        if (isErase) {
            PaintFactory.newDefaultPaintEraser(strokeWidth)
        } else {
            PaintFactory.newDefaultPaintBrush(color, strokeWidth)
        }
    }

    override fun Paint.write(parcel: Parcel, flags: Int) {
        parcel.writeInt(this.color)
        parcel.writeFloat(this.strokeWidth)
        ParcelCompat.writeBoolean(parcel, this.xfermode == PaintFactory.ERASER_XFER_MODE)
    }
}

private fun Paint.similarTo(other: Paint): Boolean =
    this.isAntiAlias == other.isAntiAlias &&
            this.xfermode == other.xfermode &&
            this.isDither == other.isDither &&
            this.style == other.style &&
            this.strokeJoin == other.strokeJoin &&
            this.strokeCap == other.strokeCap &&
            this.color == other.color &&
            this.alpha == other.alpha &&
            this.strokeWidth == other.strokeWidth

@Suppress("MagicNumber")
private fun Paint.similarHashCode(): Int {
    var result = isAntiAlias.hashCode()
    result = 31 * result + xfermode.hashCode()
    result = 31 * result + isDither.hashCode()
    result = 31 * result + style.hashCode()
    result = 31 * result + strokeJoin.hashCode()
    result = 31 * result + strokeCap.hashCode()
    result = 31 * result + color.hashCode()
    result = 31 * result + alpha.hashCode()
    result = 31 * result + strokeWidth.hashCode()
    return result
}
