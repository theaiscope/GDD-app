package net.aiscope.gdd_app.repository

import com.google.firebase.auth.FirebaseAuth
import net.aiscope.gdd_app.model.Microscopist
import javax.inject.Inject

class FirebaseMicroscopistRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val store: SharedPreferenceStore
) : MicroscopistRepository {

    override fun load(): Microscopist {
        val microscopistId = firebaseAuth.currentUser?.uid
            ?: throw IllegalStateException("Microscopist is not logged in")
        val hasAcceptedPrivacyPolicy = store.load(
            hasAcceptedPrivacyPolicyKeyFor(microscopistId)
        ).toBoolean()
        return Microscopist(
            microscopistId,
            hasAcceptedPrivacyPolicy
        )
    }

    override fun store(microscopist: Microscopist) {
        store.store(
            hasAcceptedPrivacyPolicyKeyFor(microscopist.id),
            microscopist.hasAcceptedPrivacyPolicy.toString()
        )
    }

    private fun hasAcceptedPrivacyPolicyKeyFor(microscopistId: String) =
        "has_accepted_privacy_policy_$microscopistId"
}
