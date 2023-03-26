import React, { useState, useEffect, useMemo } from "react";
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend } from "recharts";
import { FormControl, InputLabel, Select, MenuItem } from "@material-ui/core";

const BarChartComponent = ({ receipts }) => {
  const [selectedYear, setSelectedYear] = useState("");
  const [filteredData, setFilteredData] = useState([]);
  const annual_totals = {};
  const transport_totals = {};
  const groceries_totals = {};
  const utilities_totals = {};
  const other_totals = {};


  const data = useMemo(() => {
    return receipts
      .map((receipt) => {
        const month = receipt.date.slice(3, 5);
        const year = receipt.date.slice(6, 10);
        const monthNames = [
          "Jan",
          "Feb",
          "Mar",
          "Apr",
          "May",
          "Jun",
          "Jul",
          "Aug",
          "Sep",
          "Oct",
          "Nov",
          "Dec",
        ];
        const monthName = monthNames[Number(month) - 1];
        const total = receipt.quantities.reduce((acc, quantity, index) => {
          return acc + quantity * receipt.totals[index];
        }, 0);

        return {
          month,
          monthName,
          year,
          total,
        };
      })
      .sort((a, b) => {
        // Sort the data by year and month
        if (a.year === b.year) {
          return a.month - b.month;
        } else {
          return a.year - b.year;
        }
      });

  }, [receipts]);

  //Calculate annual totals and add to annual_totals array for use in the Select component, using the year as the key
  data.forEach((item) => {
    if (annual_totals[item.year]) {
      annual_totals[item.year] += item.total;
    } else {
      annual_totals[item.year] = item.total;
    }
  });
  
  useEffect(() => {
    const filtered = data.filter((item) => {
      return item.year === selectedYear;
    });
    setFilteredData(filtered);
  }, [selectedYear, data]);

  const yearOptions = useMemo(() => {
    return [
      ...new Set(data.map((item) => item.year))].map((year) => (
      <MenuItem key={year} value={year}>
        {year}
      </MenuItem>
    ));
  }, [data]);

  return (
    <div style={{ margin: "10px", padding: "10px", display: "flex", flexDirection: "column", alignItems: "center"}}>
      <FormControl variant="outlined" fullWidth style={{ marginBottom: "20px" }}>
        <InputLabel id="year-selector-label">Select year to view BarChart
</InputLabel>
        <Select
          labelId="year-selector-label"
          id="year-selector"
          displayEmpty
          value={selectedYear}
          onChange={(event) => setSelectedYear(event.target.value)}
          label="Year"
        >
          {yearOptions}
        </Select>
      </FormControl>

      {filteredData.length === 0 && selectedYear !== "" ? (
        <p>No data available for the selected year.</p>
      ) : (
        <BarChart width={window.innerWidth - 100} height={500} data={filteredData}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="monthName" />
          <YAxis />
          <Tooltip />
          <Legend />
          <Bar dataKey="total" fill="#8884d8" />
        </BarChart>        
      )}
      <p>Annual total: €{(annual_totals[selectedYear] ?? 0).toFixed(2)}</p>
      
    </div>
  );
};

export default BarChartComponent;