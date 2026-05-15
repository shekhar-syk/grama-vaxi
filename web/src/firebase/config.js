import { initializeApp } from "firebase/app";
import { getAuth } from "firebase/auth";
import { getFirestore } from "firebase/firestore";
import { getStorage } from "firebase/storage";

// Replace these placeholders with your Web App config from Firebase Console
const firebaseConfig = {
  apiKey: "AIzaSyAAXRd6OlfyN9Bb7xIHQ-ojo5klSlxtEkc",
  authDomain: "gram-vaxi-main.firebaseapp.com",
  projectId: "gram-vaxi-main",
  storageBucket: "gram-vaxi-main.firebasestorage.app",
  messagingSenderId: "1073932113490",
  appId: "PASTE_YOUR_WEB_APP_ID_HERE"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);

// Export services to use them in our pages
export const auth = getAuth(app);
export const db = getFirestore(app);
export const storage = getStorage(app);
