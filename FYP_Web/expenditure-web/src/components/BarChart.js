import React, { useState } from "react";
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend } from "recharts";
import { FormControl, InputLabel, Select, MenuItem } from "@material-ui/core";

const BarCart = ({ receipt }) => {
  const [selectedYear, setSelectedYear] = useState("All");

  // Generate mock data for the BarChart
  const data = [
    { month: "Jan", total: 1500, year: 2022 },
    { month: "Feb", total: 2500, year: 2022 },
    { month: "Mar", total: 1800, year: 2022 },
    { month: "Apr", total: 2800, year: 2022 },
    { month: "May", total: 2000, year: 2022 },
    { month: "Jun", total: 1500, year: 2022 },
    { month: "Jul", total: 2800, year: 2022 },
    { month: "Aug", total: 1800, year: 2022 },
    { month: "Sep", total: 2200, year: 2022 },
    { month: "Oct", total: 2800, year: 2022 },
    { month: "Nov", total: 2000, year: 2022 },
    { month: "Dec", total: 2500, year: 2022 },
    { month: "Jan", total: 1000, year: 2023 },
    { month: "Feb", total: 1500, year: 2023 },
    { month: "Mar", total: 2000, year: 2023 },
  ];

  // Filter data based on selected year
  const filteredData = data.filter((item) => {
    if (selectedYear === "All") {
      return true;
    } else {
      return item.year === selectedYear;
    }
  });


  const yearOptions = [...new Set(data.map((item) => item.year))].map((year) => (
    <MenuItem key={year} value={year}>
      {year}
    </MenuItem>
  ));


  return (
    <div>
      <FormControl variant="outlined" fullWidth>
        <InputLabel id="year-selector-label">Year</InputLabel>
        <Select
          labelId="year-selector-label"
          id="year-selector"
          value={selectedYear}
          onChange={(event) => setSelectedYear(event.target.value)}
          label="Year"
        >
          {yearOptions}
        </Select>
      </FormControl>
      <BarChart width={600} height={300} data={filteredData}>
        <CartesianGrid strokeDasharray="3 3" />
        <XAxis dataKey="month" />
        <YAxis />
        <Tooltip />
        <Legend />
        <Bar dataKey="total" fill="#8884d8" />
      </BarChart>
    </div>
  );
};

export default BarCart;
