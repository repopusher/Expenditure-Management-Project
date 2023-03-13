import { initializeApp } from "firebase/app";
import { getAuth, createUserWithEmailAndPassword, signInWithEmailAndPassword, signOut, sendPasswordResetEmail} from "firebase/auth";
import { getFirestore, collection, addDoc, updateDoc, doc } from "firebase/firestore";

const firebaseConfig = {
  apiKey: "AIzaSyB0jRLin-Iu-72v2hDIPPdtyAvgOJ_TWQo",
  authDomain: "expenditure-management-aaab6.firebaseapp.com",
  databaseURL: "https://expenditure-management-aaab6-default-rtdb.europe-west1.firebasedatabase.app",
  projectId: "expenditure-management-aaab6",
  storageBucket: "expenditure-management-aaab6.appspot.com",
  messagingSenderId: "1031480718349",
  appId: "1:1031480718349:web:5380a886812a8394e4d144",
  measurementId: "G-4BFPWMGZKF"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
const auth = getAuth(app)
const db = getFirestore(app);

const logInWithEmailAndPassword = async (email, password) => {
  try {
    await signInWithEmailAndPassword(auth, email, password);
  } catch (err) {
    console.error(err);
    alert(err.message);
  }
};

const registerWithEmailAndPassword = async (name, email, password) => {
  try {
    const res = await createUserWithEmailAndPassword(auth, email, password);
    const user = res.user;
    await addDoc(collection(db, "users"), {
      uid: user.uid,
      name,
      authProvider: "local",
      email,
    });
  } catch (err) {
    console.error(err);
    alert(err.message);
  }
};

//find a receipt by id and update it
const updateReceipt = async (receipt) => {
  try {
    const receiptRef = doc(db, "receipts", receipt.id);
    await updateDoc(receiptRef, receipt);
  } catch (err) {
    console.error(err);
    alert(err.message);
  }
};

const sendPasswordReset = async (email) => {
  try {
    await sendPasswordResetEmail(auth, email);
    alert("Password reset link sent!");
  } catch (err) {
    console.error(err);
    alert(err.message);
  }
};

const logout = () => {
  signOut(auth);
};

export {
  auth,
  db,
  logInWithEmailAndPassword,
  registerWithEmailAndPassword,
  sendPasswordReset,
  logout,
  updateReceipt,
};