import React, { useState, useEffect } from 'react';
import PropTypes from 'prop-types';
import { makeStyles } from '@material-ui/core/styles';
import { TextField, Button, Typography, AppBar, Toolbar } from '@material-ui/core';
import { updateReceipt } from '../config/firebase';

const useStyles = makeStyles({
  form: {
    display: 'flex',
    flexDirection: 'column',
    padding: '16px',
    maxWidth: '600px',
    margin: 'auto',
    marginTop: '60px',
    maxHeight: '500px',
  },
  input: {
    margin: '8px',
    width: '600px',
  },
  buttons: {
    display: 'flex',
    flexDirection: 'row',
    justifyContent: 'center',
    margin: '16px 0',
  },
  updateButton: {
    margin: '0 8px',
  },
});

function ReceiptEntryForm(props) {
  const classes = useStyles();
  const [rowData , setRowData] = useState({});

  useEffect(() => {
    setRowData(props.rowData);
  }, [props.rowData]);

 
  //function to clear the text fields
  const clearFields = () => {
    document.getElementById("item").value = "";
    document.getElementById("quantity").value = "";
    document.getElementById("total").value = "";
  };

  
  const handleDelete = () => {
    const receipt = rowData.receipt;
    const index = rowData.index;

    if (window.confirm("Are you sure you want to delete this entry?")) {
      receipt.items.splice(index, 1);
      receipt.quantities.splice(index, 1);
      receipt.totals.splice(index, 1);

      updateReceipt(receipt).then(() => {
        clearFields();
      });
    }
  };

  const handleUpdate = () => {
    const item = document.getElementById("item").value;
    const quantity = document.getElementById("quantity").value;
    const total = document.getElementById("total").value;
    const receipt = rowData.receipt;

    if (item !== "") {
      receipt.items[rowData.index] = item;
    }

    if (quantity !== "") {
      if (isNaN(quantity) || !Number.isInteger(parseFloat(quantity))) {
        alert("Quantity must be a number and an integer");
        document.getElementById("quantity").focus();
        clearFields();
        return;
      }
      receipt.quantities[rowData.index] = parseInt(quantity);
    }

    if (total !== "") {
      if (isNaN(total)) {
        alert("Total must be a number");
        document.getElementById("total").focus();
        clearFields();
        return;
      }
      receipt.totals[rowData.index] = parseFloat(total).toFixed(2);
    }

    updateReceipt(receipt).then(() => {
      clearFields();
    });

  };
  

  return (
    <form className={classes.form}>

      <Typography variant="h6">Change/Delete an Entry</Typography>
      <label>Item</label>
      <TextField
        id="item"
        className={classes.input}
        variant="outlined"
        label={props.rowData.receipt.items[props.rowData.index]}
      />
      <label>Price</label>
      <TextField
        id="total"
        className={classes.input}
        variant="outlined"
        label={"€" + props.rowData.receipt.totals[props.rowData.index]}
      />
      <label>Quantity</label>
      <TextField
        id="quantity"
        className={classes.input}
        variant="outlined"
        label={props.rowData.receipt.quantities[props.rowData.index]}
      />
      
      <div className={classes.buttons}>
        <Button variant="contained" color="primary" className={classes.updateButton} onClick={handleUpdate}>
          Update
        </Button>
        <Button variant="contained" color="secondary" onClick={handleDelete}>
          Delete
        </Button>
      </div>
    </form>
  );
}

ReceiptEntryForm.propTypes = {
  rowData: PropTypes.object.isRequired,
};

export default ReceiptEntryForm;
