package net.aiscope.gdd_app.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import dagger.android.AndroidInjection
import net.aiscope.gdd_app.ui.main.MainActivity
import javax.inject.Inject


class LoginActivity : AppCompatActivity(), LoginView {

    @Inject
    lateinit var presenter: LoginPresenter

    private val logIn = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        presenter.checkActivityResult(it.resultCode, it.data)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        presenter.start(logIn)
    }

    override fun exit() {
        finish()
    }

    override fun goIn() {
        finish()
        startActivity(Intent(this, MainActivity::class.java))
    }
}
