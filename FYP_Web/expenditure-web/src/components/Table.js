import React, { useState } from 'react';
import PropTypes from 'prop-types';
import { makeStyles } from '@material-ui/core/styles';
import { Tabs, Tab, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper } from '@material-ui/core';

const useStyles = makeStyles({
  root: {
    display: 'flex',
    flexDirection: 'column',
    padding: '16px',
    maxWidth: '700px',
    maxHeight: '500px',
    marginTop: '60px',
    margin: 'auto',
  },
  container: {
    maxHeight: 400,
    maxWidth: 700,
  },
  tabs: {
    width: 700,
    paddingLeft: 0,
    paddingRight: 0,
    '& .MuiTab-root': {
      width: '25%',
    },
  },
});


function TableComponent(props) {
  const classes = useStyles();
  const [value, setValue] = useState(0);
  const { receipts } = props;

  const getCategoryTabIndex = (category) => {
    switch (category) {
      case "groceries":
        return 0;
      case "utilities":
        return 1;
      case "transport":
        return 2;
      default:
        return 3;
    }
  };

  return (
    <div className={classes.root}>
      <Tabs value={value} onChange={(event, newValue) => setValue(newValue)} centered className={classes.tabs}>
        <Tab label="Groceries" />
        <Tab label="Utilities" />
        <Tab label="Transport" />
        <Tab label="Other" />
      </Tabs>
      
      {[...Array(4).keys()].map((index) => (
        <TabPanel value={value} index={index} key={index}>
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
                {receipts.map((receipt) => {
                  if (getCategoryTabIndex(receipt.category) !== index) return null;
                  return receipt.items.map((item, index) => (
                    <TableRow hover role="checkbox" tabIndex={-1} key={index} onClick={(event) => props.onRowClick(event, {receipt, index})}>
                      <TableCell component="th" scope="row">
                        {item}
                      </TableCell>
                      <TableCell align="right">{receipt.quantities[index]}</TableCell>
                      <TableCell align="right">{receipt.totals[index]}</TableCell>
                      <TableCell align="right">{receipt.date}</TableCell>
                    </TableRow>
                  ));
                })}
              </TableBody>
              
            </Table>
          </TableContainer>
        </TabPanel>
      ))}
    </div>
  );
}

function TabPanel(props) {
  const { children, value, index, ...other } = props;

  return (
    <div role="tabpanel" hidden={value !== index} id={`scrollable-auto-tabpanel-${index}`} aria-labelledby={`scrollable-auto-tab-${index}`} {...other}>
      {value === index && (
        <div>
          {children}
        </div>
      )}
    </div>
  );
}

TableComponent.propTypes = {
  receipts: PropTypes.array.isRequired,
};

export default TableComponent;