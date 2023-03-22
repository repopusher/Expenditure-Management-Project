import React, { useState } from 'react';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend } from 'recharts';
import { FormControl, InputLabel, Select, MenuItem } from '@material-ui/core';

const data = [
  { month: 'Jan', spend: 500 },
  { month: 'Feb', spend: 600 },
  { month: 'Mar', spend: 700 },
  { month: 'Apr', spend: 800 },
  { month: 'May', spend: 900 },
  { month: 'Jun', spend: 1000 },
  { month: 'Jul', spend: 1100 },
  { month: 'Aug', spend: 1200 },
  { month: 'Sep', spend: 1300 },
  { month: 'Oct', spend: 1400 },
  { month: 'Nov', spend: 1500 },
  { month: 'Dec', spend: 1600 }
];

const BarChartComponent = () => {
  const [year, setYear] = useState('2021');
  const filteredData = data.filter((item) => item.month <= year);

  const handleYearChange = (event) => {
    setYear(event.target.value);
  };

  return (
    <div>
      <FormControl>
        <InputLabel>Year</InputLabel>
        <Select value={year} onChange={handleYearChange}>
          <MenuItem value="2021">2021</MenuItem>
          <MenuItem value="2022">2022</MenuItem>
          <MenuItem value="2023">2023</MenuItem>
        </Select>
      </FormControl>
      <BarChart width={600} height={300} data={filteredData}>
        <CartesianGrid strokeDasharray="3 3" />
        <XAxis dataKey="month" />
        <YAxis />
        <Tooltip />
        <Legend />
        <Bar dataKey="spend" fill="#8884d8" />
      </BarChart>
    </div>
  );
};

export default BarChartComponent;