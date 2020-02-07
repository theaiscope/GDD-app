import * as admin from 'firebase-admin';
import * as functions from 'firebase-functions';
import UserRecord = admin.auth.UserRecord;

admin.initializeApp(functions.config().firebase);

const db = admin.firestore();

export const onNewUserCreated = functions
    .region("europe-west1")
    .auth
    .user()
    .onCreate((user, context) => {
        return saveMicroscopistToFirestore(user)
            .catch(error => {
                console.error("Error writing document: ", error);
            });
    });

const saveMicroscopistToFirestore = (user: UserRecord) => db.collection("microscopists")
    .doc(user.uid)
    .set({
        enabled: false
    })
    .then(function () {
        console.log("Document successfully written!");
    });
