package net.aiscope.gdd_app.ui.snackbar

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.snackbar.ContentViewCallback
import net.aiscope.gdd_app.R

class CustomSnackbarView @JvmOverloads constructor(
    context: Context,
    attributes: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attributes, defStyleAttr), ContentViewCallback {

    companion object {
        const val ANIMATION_SCALE_INITIAL_VALUE = 0f
        const val ANIMATION_SCALE_FINAL_VALUE = 1f
    }

    var messageLabel: TextView
    var actionLabel: TextView
    var icon: ImageView
    private var root: ConstraintLayout

    init {
        View.inflate(context, R.layout.custom_snackbar, this)
        clipToPadding = false
        this.messageLabel = findViewById(R.id.message_label)
        this.actionLabel = findViewById(R.id.action_label)
        this.icon = findViewById(R.id.icon)
        this.root = findViewById(R.id.snack_constraint)
    }

    override fun animateContentIn(delay: Int, duration: Int) {
        val scaleX = ObjectAnimator
            .ofFloat(icon, View.SCALE_X, ANIMATION_SCALE_INITIAL_VALUE, ANIMATION_SCALE_FINAL_VALUE)
        val scaleY = ObjectAnimator
            .ofFloat(icon, View.SCALE_Y, ANIMATION_SCALE_INITIAL_VALUE, ANIMATION_SCALE_FINAL_VALUE)
        val animatorSet = AnimatorSet().apply {
            interpolator = OvershootInterpolator()
            setDuration(duration.toLong())
            playTogether(scaleX, scaleY)
        }
        animatorSet.start()
    }

    override fun animateContentOut(delay: Int, duration: Int) = Unit
}
