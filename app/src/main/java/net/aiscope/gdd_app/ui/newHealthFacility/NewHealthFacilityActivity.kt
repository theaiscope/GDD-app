package net.aiscope.gdd_app.ui.newHealthFacility

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_new_health_facility.*
import net.aiscope.gdd_app.R
import javax.inject.Inject


class NewHealthFacilityActivity : AppCompatActivity(), NewHealthFacilityView {

    @Inject lateinit var presenter: NewHealthFacilityPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_health_facility)

        button_save_new_health_facility.setOnClickListener {
            presenter.saveHospital(text_health_facility_name_field.text.toString())
        }

        button_cancel_new_health_facility.setOnClickListener {
            presenter.destroyActivity()
        }

    }

    override fun showToast(messageId: Int) {
        val toast = Toast.makeText(this, messageId, Toast.LENGTH_SHORT)
        toast.show()
    }

    override fun destroy() {
        this.finish()
    }



}
