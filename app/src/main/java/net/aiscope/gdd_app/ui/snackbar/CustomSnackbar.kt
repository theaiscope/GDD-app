package net.aiscope.gdd_app.ui.snackbar

import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.BaseTransientBottomBar
import kotlinx.android.synthetic.main.custom_snackbar.view.*
import net.aiscope.gdd_app.R

class SimpleCustomSnackbar (
    parent: ViewGroup,
    content: CustomSnackbarView
) : BaseTransientBottomBar<SimpleCustomSnackbar>(parent, content, content) {

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
        ): SimpleCustomSnackbar {

            val parent = findSuitableParent(view) ?: throw IllegalArgumentException(
                "No suitable parent found from the given view. Please provide a valid view."
            )

            val customView = LayoutInflater.from(view.context).inflate(
                R.layout.custom_snackbar_inflation,
                parent,
                false
            ) as CustomSnackbarView

            with(customView) {
                tv_message.text = message
                action?.let {
                    tvAction.text = action.label
                    tvAction.setOnClickListener {
                        action.listener.onClick(customView.tvAction)
                    }
                }
                icon?.let {
                    imLeft.visibility = VISIBLE
                    imLeft.setImageResource(it)
                }
            }

            return SimpleCustomSnackbar(
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
