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

  const fetchUserReceipts = async () => {
    try {
      const q = query(collectionReceiptReference, where("uid", "==", user?.uid));
      const doc = await getDocs(q);
      const data = doc.docs[0].data();

      console.log(data.item, data.date, data.total);
    } catch (err) {
      console.log(err);
      alert("An error occured while fetching receipt data");
    }
  };

  useEffect(() => {
    function fetchBusinesses(){

        if (loading) return;

        if (!user) return navigate('/', {replace: true});

        fetchUserName();
        fetchUserReceipts();
    }

    fetchBusinesses()

  }, [user, loading, navigate]);

  return (
    <div className="dashboard">
       <div className="dashboard__container">

          <p>Logged in as </p>
          <TableComponent/>
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