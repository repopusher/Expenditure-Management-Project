import React, { useState } from 'react';
import PropTypes from 'prop-types';
import { makeStyles } from '@material-ui/core/styles';
import { Tabs, Tab, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper } from '@material-ui/core';

const useStyles = makeStyles({
  root: {
    flexGrow: 1,
  },
  container: {
    maxHeight: 440,
    maxWidth: 700,
  },
});

function createData(item, quantity, price, date) {
  return { item, quantity, price, date };
}

const rows = [
  createData('Item 1', 1, 10.99, '2022-01-01'),
  createData('Item 2', 2, 20.99, '2022-01-02'),
  createData('Item 3', 3, 30.99, '2022-01-03'),
];

function TableComponent() {
  const classes = useStyles();
  const [value, setValue] = useState(0);

  const handleChange = (event, newValue) => {
    setValue(newValue);
  };

  return (
    <div className={classes.root}>
      <Tabs value={value} onChange={handleChange} centered>
        <Tab label="Groceries" />
        <Tab label="Utilities" />
        <Tab label="Transport" />
        <Tab label="Other" />
      </Tabs>
      <TableContainer component={Paper} className={classes.container}>
        <Table stickyHeader aria-label="sticky table">
          <TableHead>
            <TableRow>
              <TableCell>Item</TableCell>
              <TableCell align="right">Quantity</TableCell>
              <TableCell align="right">Price</TableCell>
              <TableCell align="right">Date</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {rows.map((row) => (
              <TableRow key={row.item}>
                <TableCell component="th" scope="row">
                  {row.item}
                </TableCell>
                <TableCell align="right">{row.quantity}</TableCell>
                <TableCell align="right">{row.price}</TableCell>
                <TableCell align="right">{row.date}</TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    </div>
  );
}

TableComponent.propTypes = {
  children: PropTypes.node,
};

export default TableComponent;
