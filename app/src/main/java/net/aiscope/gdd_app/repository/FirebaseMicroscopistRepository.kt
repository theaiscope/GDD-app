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
            ?: error("Microscopist is not logged in")
        val hasAcceptedPrivacyPolicy = store.load(
            hasAcceptedPrivacyPolicyKeyFor(microscopistId)
        ).toBoolean()
        val hasSubmitSampleFirstTime = store.load(
            hasSubmitSampleFirstTimeFor(microscopistId)
        ).toBoolean()
        return Microscopist(
            microscopistId,
            hasAcceptedPrivacyPolicy,
            hasSubmitSampleFirstTime
        )
    }

    override fun store(microscopist: Microscopist) {
        store.store(
            hasAcceptedPrivacyPolicyKeyFor(microscopist.id),
            microscopist.hasAcceptedPrivacyPolicy.toString()
        )
        store.store(
            hasSubmitSampleFirstTimeFor(microscopist.id),
            microscopist.hasSubmitSampleFirstTime.toString()
        )
    }



    private fun hasAcceptedPrivacyPolicyKeyFor(microscopistId: String) =
        "has_accepted_privacy_policy_$microscopistId"

    private fun hasSubmitSampleFirstTimeFor(microscopistId: String) =
        "has_submit_sample_first_time_$microscopistId"
}
