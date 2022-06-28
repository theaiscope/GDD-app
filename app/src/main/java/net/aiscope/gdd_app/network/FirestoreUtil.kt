package net.aiscope.gdd_app.network
import com.google.firebase.firestore.FirebaseFirestore

class FirestoreUtil {
    object FirestoreUtil {
        private var FIRESTORE: FirebaseFirestore? = null
        val firestore: FirebaseFirestore?
            get() {
                if (FIRESTORE == null) {
                    FIRESTORE = FirebaseFirestore.getInstance()
                }
                return FIRESTORE
            }
    }
}