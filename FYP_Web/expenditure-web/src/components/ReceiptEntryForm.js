import React, { useState, useEffect } from 'react';
import PropTypes from 'prop-types';
import { makeStyles } from '@material-ui/core/styles';
import { TextField, Button, Typography } from '@material-ui/core';

const useStyles = makeStyles({
  form: {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    justifyContent: 'center',
    padding: '16px',
    maxWidth: '600px',
    margin: 'auto',
  },
  input: {
    margin: '8px',
    width: '100%',
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

  //handleUpdate checks if the user has entered a new value for any of the fields, if so, it uses the 
  const handleUpdate = () => {
    //call to firebase to update the receipt will add later lol
  };

  return (
    <form className={classes.form}>
      <Typography variant="h6">Change/Delete an Entry</Typography>
      <TextField
        className={classes.input}
        variant="outlined"
        label={props.rowData.receipt.items[props.rowData.index]}
      />
      <TextField
        className={classes.input}
        variant="outlined"
        label={props.rowData.receipt.totals[props.rowData.index]}
      />
      <TextField
        className={classes.input}
        variant="outlined"
        label={props.rowData.receipt.quantities[props.rowData.index]}
      />
      
      <div className={classes.buttons}>
        <Button variant="contained" color="primary" className={classes.updateButton} onClick={handleUpdate}>
          Update
        </Button>
        <Button variant="contained" color="secondary">
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
