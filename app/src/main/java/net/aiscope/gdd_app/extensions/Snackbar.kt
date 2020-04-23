package net.aiscope.gdd_app.extensions

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.text.color
import androidx.core.text.scale
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import net.aiscope.gdd_app.R

const val SNACKBAR_TEXT_SCALE = 1.2f

fun Snackbar.setActionFontType(typeface: Int): Snackbar {
    val actionTextView = this.view.findViewById(R.id.snackbar_action) as TextView

    actionTextView.setTypeface(actionTextView.typeface, typeface)

    return this
}

fun Context.buildSnackbarContent(@StringRes text: Int, @DrawableRes icon: Int? = null): SpannableStringBuilder {
    val builder = SpannableStringBuilder()
    if(icon != null) {
        builder.append("  ")
        builder.setSpan(ImageSpan(this, icon), builder.length - 1, builder.length, 0)
        builder.append("  ")
    }
    builder.color(getColor(R.color.colorPlainText)) { scale(SNACKBAR_TEXT_SCALE) { append(getString(text)) } }
    return builder
}
