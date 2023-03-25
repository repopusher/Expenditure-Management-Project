import React, { useState, useEffect, useMemo } from "react";
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend } from "recharts";
import { FormControl, InputLabel, Select, MenuItem } from "@material-ui/core";

const BarCart = ({ receipt }) => {
  const currentYear = new Date().getFullYear(); // get the current year
  const [selectedYear, setSelectedYear] = useState("");
  const [filteredData, setFilteredData] = useState([]);

  const data = useMemo(() => [
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
  ], []);

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
        <InputLabel shrink={true} id="year-selector-label">Year</InputLabel>
        <Select
          labelId="year-selector-label"
          id="year-selector"
          displayEmpty
          value={selectedYear}
          onChange={(event) => setSelectedYear(event.target.value)}
          label="Year"
        >
          <MenuItem value="">
            Select year to view BarChart
          </MenuItem>
          {yearOptions}
        </Select>
      </FormControl>

      {filteredData.length === 0 && selectedYear !== "" ? (
        <p>No data available for the selected year.</p>
      ) : (
        <BarChart width={window.innerWidth - 100} height={500} data={filteredData}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="month" />
          <YAxis />
          <Tooltip />
          <Legend />
          <Bar dataKey="total" fill="#8884d8" />
        </BarChart>
      )}
    </div>
  );
};

export default BarCart;