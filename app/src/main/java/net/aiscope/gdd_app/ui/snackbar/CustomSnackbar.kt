package net.aiscope.gdd_app.ui.snackbar

import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.BaseTransientBottomBar
import net.aiscope.gdd_app.R

class CustomSnackbar (
    parent: ViewGroup,
    content: CustomSnackbarView
) : BaseTransientBottomBar<CustomSnackbar>(parent, content, content) {

    init {
        getView().setBackgroundColor(
            ContextCompat.getColor(
                view.context,
                android.R.color.transparent
            )
        )
        getView().setPadding(0, 0, 0, 0)
    }

    companion object {

        fun make(
            view: View,
            message: String,
            duration: Int,
            icon: Int? = null,
            action: CustomSnackbarAction? = null
        ): CustomSnackbar {

            val parent = findSuitableParent(view) ?: throw IllegalArgumentException(
                "No suitable parent found from the given view. Please provide a valid view."
            )

            val customView = LayoutInflater.from(view.context).inflate(
                R.layout.custom_snackbar_inflation,
                parent,
                false
            ) as CustomSnackbarView

            with(customView) {
                messageLabel.text = message
                action?.let {
                    this.actionLabel.text = action.label
                    this.actionLabel.setOnClickListener {
                        action.listener.onClick(customView.actionLabel)
                    }
                }
                icon?.let {
                    this.icon.visibility = VISIBLE
                    this.icon.setImageResource(it)
                }
            }

            return CustomSnackbar(
                parent,
                customView
            ).setDuration(duration)
        }

        private fun findSuitableParent(view: View?): ViewGroup? {
            var fallback: ViewGroup? = null

            when (view) {
                is CoordinatorLayout -> {
                    return view
                }
                is FrameLayout -> {
                    if (view.id == android.R.id.content) {
                        return view
                    } else {
                        fallback = view
                    }
                }
            }

            val parentView = view?.parent as View?

            return if(parentView == null) fallback else findSuitableParent(parentView)

        }

    }
}
