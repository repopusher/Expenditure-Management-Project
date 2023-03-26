import React, { useEffect, useState, useMemo, useCallback } from "react";
import { useAuthState } from "react-firebase-hooks/auth";
import { useNavigate } from "react-router-dom";
import { auth, db, logout } from "../config/firebase";
import { query, collection, getDocs, where } from "firebase/firestore";
import TableComponent from "./Table";
import Form from "./Form";
import BarChartComponent from "./BarChart";
import { AppBar, Toolbar } from "@material-ui/core";

function Dashboard() {
  const [user, loading] = useAuthState(auth);
  const [name, setName] = useState("");
  const [receipts, setReceipts] = useState([]);
  const [rowData, setRowData] = useState({});

  const userStyles = () => ({
    display: "flex",
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    backgroundColor: "#f5f5f5",
    padding: "20px",
  });

  const tableContainerStyles = () => ({
    display: "flex",
    flexWrap: "wrap",
    justifyContent: "space-between",
  });

  const navigate = useNavigate();

  const mapReceiptData = useMemo(
    () => (data) => {
      return {
        id: data.id,
        uid: data.uid,
        category: data.category,
        date: data.date,
        items: data.items,
        quantities: data.quantities,
        totals: data.totals,
      };
    },
    []
  );

  const handleRowClick = useCallback((event, rowData) => {
    setRowData(rowData);
  }, []);

  useEffect(() => {
    async function fetchUserData() {
      try {
        if (!user) return navigate("/", { replace: true });

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
      <AppBar position="static">
        <Toolbar>
          <div>
            <p>Welcome {name}</p>
            <div>{user?.email}</div>
          </div>
          <button className="dashboard__btn" onClick={logout}>
            Logout
          </button>
        </Toolbar>
      </AppBar>
      <div className="dashboard__container">
        <div style={tableContainerStyles()}>
          <TableComponent receipts={receipts} onRowClick={handleRowClick} />
          {Object.keys(rowData).length !== 0 && <Form rowData={rowData} />}
        </div>
        {receipts.length > 0 && <BarChartComponent receipts={receipts} />}
      </div>
    </div>
  );

}

export default Dashboard;
