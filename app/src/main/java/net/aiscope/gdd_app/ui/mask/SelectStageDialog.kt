package net.aiscope.gdd_app.ui.mask

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListAdapter
import androidx.fragment.app.DialogFragment
import net.aiscope.gdd_app.R
import net.aiscope.gdd_app.ui.selector.customview.SelectorListItemCustomView

class SelectStageDialog(
    private val items: Array<BrushDiseaseStage>,
    private val presenter: MaskPresenter,
    private val view: MaskView
) :
    DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreate(savedInstanceState)

        val adapter: ListAdapter = object : ArrayAdapter<BrushDiseaseStage>(
            context!!,
            R.layout.select_stage_dialog,
            R.id.select_stage_dialog_checkedtextview,
            items
        ) {
            override fun getItemId(position: Int): Long {
                return super.getItemId(items[position].id)
            }

            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view: View = super.getView(position, convertView, parent)
                findSelectorListItem(view).apply {
                    setColor(items[position].maskColor)
                }
                return view
            }

            private fun findSelectorListItem(view: View): SelectorListItemCustomView =
                view.findViewById(R.id.select_stage_dialog_checkedtextview)
        }

        val checkedItem = items.find { item -> item.id == presenter.getBrushDiseaseStage().id }
        val currentItemIndex = items.indexOf(checkedItem)
        var latestSelection = currentItemIndex
        return AlertDialog.Builder(activity, R.style.AppTheme_Dialog)
            .setSingleChoiceItems(adapter, currentItemIndex) { _, which -> latestSelection = which }
            .setNegativeButton(getString(R.string.button_cancel), null)
            .setPositiveButton(getString(R.string.button_ok)) { _, _ ->
                if (latestSelection != currentItemIndex) {
                    view.setBrushDiseaseStage(items[latestSelection])
                }
            }
            .setTitle(R.string.mask_activity_stages_dialog_title)
            .create()
    }
}