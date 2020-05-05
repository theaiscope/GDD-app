package net.aiscope.gdd_app.ui.mask

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ListAdapter
import android.widget.ListPopupWindow
import net.aiscope.gdd_app.R

class SelectStagePopup(
    private val context: Context,
    private val brushDiseaseStages: Array<BrushDiseaseStage>,
    anchorView: View,
    onItemClickListener: AdapterView.OnItemClickListener
) :
    ListPopupWindow(context) {

    private val adapter = object : ArrayAdapter<BrushDiseaseStage>(
        context,
        R.layout.popup_select_stage_item,
        android.R.id.text1,
        brushDiseaseStages
    ) {
        override fun getItemId(position: Int): Long =
            super.getItemId(brushDiseaseStages[position].id)

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view: View = super.getView(position, convertView, parent)
            view.findViewById<ImageView>(android.R.id.icon).apply {
                colorFilter = PorterDuffColorFilter(
                    brushDiseaseStages[position].maskColor,
                    PorterDuff.Mode.SRC_IN
                )
            }
            return view
        }
    }

    init {
        this.setAdapter(adapter)
        this.setOnItemClickListener { parent, view, position, id ->
            onItemClickListener.onItemClick(parent, view, position, id)
            dismiss()
        }
        this.anchorView = anchorView
        this.setContentWidth(measureContentWidth(adapter, context))
    }

    companion object {
        private fun measureContentWidth(listAdapter: ListAdapter, context: Context): Int {
            var measureParent: ViewGroup? = null
            var maxWidth = 0
            var itemView: View? = null
            var itemType = 0
            val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            val heightMeasureSpec =
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            for (i in 0 until listAdapter.count) {
                val positionType = listAdapter.getItemViewType(i)
                if (positionType != itemType) {
                    itemType = positionType
                    itemView = null
                }
                measureParent = measureParent ?: FrameLayout(context)
                itemView = listAdapter.getView(i, itemView, measureParent)
                itemView.measure(widthMeasureSpec, heightMeasureSpec)
                maxWidth = maxWidth.coerceAtLeast(itemView.measuredWidth)
            }
            return maxWidth
        }
    }
}
