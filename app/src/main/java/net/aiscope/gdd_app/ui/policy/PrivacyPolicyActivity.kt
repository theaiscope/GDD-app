package net.aiscope.gdd_app.ui.policy

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.WorkerThread
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import dagger.android.AndroidInjection
import net.aiscope.gdd_app.R
import net.aiscope.gdd_app.databinding.ActivityPrivacyPolicyBinding
import net.aiscope.gdd_app.ui.main.MainActivity
import javax.inject.Inject


class PrivacyPolicyActivity : AppCompatActivity(), PrivacyPolicyView {

    @Inject
    lateinit var presenter: PrivacyPolicyPresenter

    private lateinit var binding: ActivityPrivacyPolicyBinding

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        binding = ActivityPrivacyPolicyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        setUpListeners()
        setUpWebView()

        presenter.start()
    }

    private fun setUpListeners() = with(binding) {
        error.setOnClickListener {
            presenter.onRetryClicked()
        }
        agreeButton.setOnClickListener {
            presenter.onPolicyAgreed()
        }
        cancelButton.setOnClickListener {
            finish()
        }
    }

    override fun goToMain() {
        finish()
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun setUpWebView() {
        WebView.setWebContentsDebuggingEnabled(true)
        with(binding.webView) {
            settings.javaScriptEnabled = true
            webViewClient = object : WebViewClient() {

                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    return true
                }
            }
            addJavascriptInterface(PrivacyPolicyLoadCallback(presenter), "privacyPolicyLoadCallback")
        }
    }

    override fun loadPolicy() {
        binding.webView.loadUrl("file:///android_asset/privacy_policy.html")
    }

    override fun showLoading() = with(binding) {
        error.isVisible = false
        agreeButton.isVisible = false
        cancelButton.isVisible = false
        progressBar.isVisible = true
    }

    override fun showLoaded() = with(binding) {
        error.isVisible = false
        agreeButton.isVisible = true
        cancelButton.isVisible = true
        progressBar.isVisible = false
    }

    override fun showError(errorMessage: String?) = with(binding) {
        error.isVisible = true
        error.text = getString(R.string.privacy_policy_error_message, errorMessage)
        agreeButton.isVisible = false
        cancelButton.isVisible = false
        progressBar.isVisible = false
    }
}

class PrivacyPolicyLoadCallback(val presenter: PrivacyPolicyPresenter) {

    @JavascriptInterface
    @WorkerThread
    fun policyLoaded() = invokeOnMainThread {
        presenter.onPolicyLoaded()
    }

    @JavascriptInterface
    @WorkerThread
    fun error(errorMessage: String) = invokeOnMainThread {
        presenter.onError(errorMessage)
    }

    private fun invokeOnMainThread(action: () -> Unit) = Handler(Looper.getMainLooper())
        .post { action() }
}
