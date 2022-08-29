import { getDatabase } from "@firebase/database";
import { initializeApp } from "firebase/app";
import { getAuth } from "firebase/auth";

const firebaseConfig = {
  apiKey: "AIzaSyDicvZpykosvJ9QYn9c8pd33SXi4jGM-y8",
  authDomain: "push-508bd.firebaseapp.com",
  projectId: "push-508bd",
  storageBucket: "push-508bd.appspot.com",
  messagingSenderId: "630331257007",
  appId: "1:630331257007:web:50a4deb8cf88f50265245a",
  databaseURL: "https://push-508bd-default-rtdb.europe-west1.firebasedatabase.app/",
};

const app = initializeApp(firebaseConfig);
const auth = getAuth(app);
const database = getDatabase(app);

export function getFirebaseAuth() {
    return auth;
}

export function getFirebaseDatabase() {
  return database;
}
