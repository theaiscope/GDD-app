package net.aiscope.gdd_app.ui.snackbar

import android.view.LayoutInflater
import android.view.View
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
            message: String, duretion: Int,
            listener: View.OnClickListener?, icon: Int, action_lable: String?, bg_color: Int
        ): SimpleCustomSnackbar? {

            // First we find a suitable parent for our custom view
            val parent = findSuitableParent(view) ?: throw IllegalArgumentException(
                "No suitable parent found from the given view. Please provide a valid view."
            )

            // We inflate our custom view
            val customView = LayoutInflater.from(view.context).inflate(
                R.layout.custom_view_inflation,
                parent,
                false
            ) as CustomSnackbarView
            // We create and return our Snackbar
            customView.tv_message.text = message
            action_lable?.let {
                customView.tvAction.text = action_lable
                customView.tvAction.setOnClickListener {
                    listener?.onClick(customView.tvAction)
                }
            }
            customView.imLeft.setImageResource(icon)
            customView.layRoot.setBackgroundColor(bg_color)

            return SimpleCustomSnackbar(
                parent,
                customView
            ).setDuration(duretion)
        }

        private fun findSuitableParent(parentView: View?): ViewGroup? {
            var view = parentView
            var fallback: ViewGroup? = null
            do {
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

                if (view != null) {
                    val parent = view.parent
                    view = if (parent is View) parent else null
                }
            } while (view != null)

            return fallback
        }

    }
}
