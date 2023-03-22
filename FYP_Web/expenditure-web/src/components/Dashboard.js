import React, { useEffect, useState, useMemo, useCallback } from "react";
import { useAuthState } from "react-firebase-hooks/auth";
import { useNavigate } from "react-router-dom";
import { auth, db, logout } from "../config/firebase";
import { query, collection, getDocs, where } from "firebase/firestore";
import TableComponent from "./Table";
import ReceiptEntryForm from "./Form";
import BarChartComponent from "./BarChart";


function Dashboard() {
  const [user, loading] = useAuthState(auth);
  const [name, setName] = useState("");
  const [receipts, setReceipts] = useState([]);
  const [rowData , setRowData] = useState({});

  const userStyles = () => ({
    display: 'flex',
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    backgroundColor: '#f5f5f5',
    padding: '20px',
  });

  const tableContainerStyles = () => ({
    display: 'flex',
    flexWrap: 'wrap',
    justifyContent: 'space-between',
  });

  const navigate = useNavigate();

  const mapReceiptData = useMemo(() => (data) => {
    return {
      id: data.id,
      uid: data.uid,
      category: data.category,
      date: data.date,
      items: data.items,
      quantities: data.quantities,
      totals: data.totals
    };
  }, []);

  const handleRowClick = useCallback((event, rowData) => {
    setRowData(rowData);
  }, []);

  useEffect(() => {
    //A function that fetches user and receipt data from firestore and sets it to state. The receipt data has a listener attached to it so that it updates in real time.  
    async function fetchUserData() {
      try {
        if (!user) return navigate('/', {replace: true});

        const qName = query(collection(db, "users"), where("uid", "==", user?.uid));
        const qReceipts = query(collection(db, "receipts"), where("uid", "==", user?.uid));

        const [nameDoc, receiptsDoc] = await Promise.all([getDocs(qName), getDocs(qReceipts)]);

        const nameData = nameDoc.docs[0].data();
        setName(nameData.name);

        const receiptData = receiptsDoc.docs.map((doc) => {
        const receipt = mapReceiptData(doc.data());
        receipt.id = doc.id;
        return receipt;
        });
        setReceipts(receiptData);
      } catch (err) {
        console.error(err);
        alert("An error occured while fetching user data");
      }
    }

    fetchUserData();
  }, [user, navigate, mapReceiptData]);

  return (
    <div className="dashboard">
    <div className="dashboard__container">
      <div style={userStyles()}>
        <div>
          <p>Logged in as </p>
          <div>{name}</div>
          <div>{user?.email}</div>
        </div>
        <button className="dashboard__btn" onClick={logout}>
          Logout
        </button>
      </div>
      <div style={tableContainerStyles()}>
        <TableComponent receipts={receipts} onRowClick={handleRowClick} />
        {Object.keys(rowData).length !== 0 && <ReceiptEntryForm rowData={rowData} />}
      </div>
      <BarChartComponent/>
    </div>
  </div>
  );
}

export default Dashboard;
