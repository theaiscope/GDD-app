package net.aiscope.gdd_app.ui.metadata

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.os.Bundle
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import com.thejuki.kformmaster.helper.*
import com.thejuki.kformmaster.model.BaseFormElement
import com.thejuki.kformmaster.model.FormButtonElement
import com.thejuki.kformmaster.model.FormEmailEditTextElement
import com.thejuki.kformmaster.model.FormPickerDropDownElement
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_metadata.*
import net.aiscope.gdd_app.R
import net.aiscope.gdd_app.ui.main.MainActivity
import javax.inject.Inject

class MetadataActivity : AppCompatActivity() , MetadataView {

    @Inject lateinit var presenter: MetadataPresenter
    lateinit var formBuilder: FormBuildHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_metadata)

        formBuilder = FormBuildHelper(this)
        formBuilder.attachRecyclerView(this, recyclerView)

        presenter.showScreen()
    }

    private fun generateFormElements(model: List<FieldModel>): List<BaseFormElement<*>> {
        val elements: MutableList<BaseFormElement<*>> = mutableListOf()

        val formModels = model.map {
            FormPickerDropDownElement<ListItem>(0).apply {
                title = getResources().getString(it.title)
                dialogTitle = getResources().getString(it.title)
                required = it.required
                options = it.options.map {
                    ListItem(id = it.id, name = getResources().getString(it.title))
                }
            }
        }
        elements.addAll(formModels)

        elements.add(FormButtonElement(1).apply {
            value = getResources().getString(R.string.metadata_add)
            valueObservers.add { _, _ ->
                save()
            }
        })

        return elements
    }

    private fun save() {
        if (! formBuilder.isValidForm) {
            presenter.notValid()
        } else {
            val values = (0..formBuilder.elements.size - 2).map {
                formBuilder.getElementAtIndex(it).value
            }
            presenter.save(values)
        }

    }

    override fun fillForm(model: List<FieldModel>) {
        val formElements = generateFormElements(model)
        formBuilder.addFormElements(formElements)
    }

    override fun showInvalidFormError() {
        Toast.makeText(this, R.string.metadata_invalid_form, Toast.LENGTH_SHORT).show()
    }

    override fun goToHome() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = FLAG_ACTIVITY_CLEAR_TOP

        startActivity(intent)
    }
}
