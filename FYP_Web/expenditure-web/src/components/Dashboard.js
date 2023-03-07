import React, { useEffect, useState } from "react";
import { useAuthState } from "react-firebase-hooks/auth";
import { useNavigate } from "react-router-dom";
import { auth, db, logout } from "../config/firebase";
import { query, collection, getDocs, where } from "firebase/firestore";
import TableComponent from "./TableComponent";

function Dashboard() {
  //Error variable if you need it in [user, loading, error]
  const [user, loading] = useAuthState(auth);
  const [name, setName] = useState("");
  const [receipts, setReceipts] = useState([]); // State variable for receipts
  const collectionReceiptReference = collection(db, "receipts")
  const collectionUserReference = collection(db, "users")

  const navigate = useNavigate();

  const fetchUserName = async () => {
    try {
      const q = query(collectionUserReference, where("uid", "==", user?.uid));
      const doc = await getDocs(q);
      const data = doc.docs[0].data();
      setName(data.name);
    } catch (err) {
      console.error(err);
      alert("An error occured while fetching user data");
    }
  };

  const mapReceiptData = (data) => {

    const receipt = {
      uid: data.uid,
      category: data.category,
      date: data.date,
      items: data.items,
      quantities: data.quantities,
      totals: data.totals
    }
    return receipt;
  };

  const fetchUserReceipts = async () => {
    try {
      const q = query(collectionReceiptReference, where("uid", "==", user?.uid));
      const doc = await getDocs(q);
      const data = doc.docs.map((doc) => mapReceiptData(doc.data()));
      return data;
    } catch (err) {
      console.error(err);
      alert("An error occured while fetching user data");
    }
  };

  useEffect(() => {
    async function fetchBusinesses() {
      if (loading) return;
      if (!user) return navigate('/', {replace: true});
      fetchUserName();
      const data = await fetchUserReceipts(); // Call fetchUserReceipts and wait for the returned data
      setReceipts(data); // Set the receipts state variable to the returned data
    }
    fetchBusinesses()
  }, [user, loading, navigate]);

  return (
    <div className="dashboard">
      <div className="dashboard__container">
        <p>Logged in as </p>
        <TableComponent receipts={receipts} /> {/* Pass the receipts state variable as a prop */}
        <div>{name}</div>
        <div>{user?.email}</div>
        <button className="dashboard__btn" onClick={logout}>
          Logout
        </button>
      </div>
    </div>
  );
}

export default Dashboard;