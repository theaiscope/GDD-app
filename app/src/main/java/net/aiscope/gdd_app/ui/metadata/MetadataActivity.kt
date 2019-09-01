package net.aiscope.gdd_app.ui.metadata

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_metadata.*
import net.aiscope.gdd_app.R
import net.aiscope.gdd_app.model.SmearType
import net.aiscope.gdd_app.ui.main.MainActivity
import java.io.File
import javax.inject.Inject

class MetadataActivity : AppCompatActivity() , MetadataView {

    @Inject lateinit var presenter: MetadataPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_metadata)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            // TODO add dialog and go to home screen on confirmation
            goToHome()
        }

        presenter.showScreen()

        metadata_save_sample.setOnClickListener {
            presenter.save(
                when(metadata_section_smear_type_radio_group.checkedRadioButtonId) {
                    R.id.metadata_blood_smear_thick -> SmearType.THICK
                    R.id.metadata_blood_smear_thin -> SmearType.THIN
                    else -> throw IllegalStateException("${metadata_section_smear_type_radio_group.checkedRadioButtonId} radio button id is unknown")
                }
             )
        }
    }

    override fun fillForm(model: ViewStateModel) {
        metadata_blood_sample_image.setImageURI(Uri.fromFile(File(model.imagePath)))
        // TODO set species stages
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
