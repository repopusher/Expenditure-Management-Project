import React, { useEffect, useState } from 'react';
import { useAuthState } from 'react-firebase-hooks/auth';
import { Link, useNavigate } from 'react-router-dom';
import { auth, registerWithEmailAndPassword } from '../config/firebase';
import { makeStyles } from '@material-ui/core/styles';
import TextField from '@material-ui/core/TextField';
import Button from '@material-ui/core/Button';
import Typography from '@material-ui/core/Typography';
import Grid from '@material-ui/core/Grid';

const useStyles = makeStyles((theme) => ({
  root: {
    '& .MuiTextField-root': {
      margin: theme.spacing(1),
      width: '100%',
    },
  },
  form: {
    width: '100%', // Fix IE 11 issue.
    marginTop: theme.spacing(1),
  },
  submit: {
    margin: theme.spacing(3, 0, 2),
  },
}));

function Register() {

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [name, setName] = useState("");
  const [user, loading] = useAuthState(auth);
  
  const navigate = useNavigate();
  const classes = useStyles();

  const register = (e) => {
    e.preventDefault();
    if (!name) alert("Please enter name");
    registerWithEmailAndPassword(name, email, password);
  };
  
  useEffect(() => {

    if (loading) {
      return;
    }
    if (user) navigate("/dashboard");

  }, [user, loading, navigate]);

  return (
    <Grid container justify="center" alignItems="center" style={{ minHeight: '100vh' }}>
      <Grid item xs={12} sm={8} md={6} lg={4}>
        <div className={classes.root}>
          <Typography component="h1" variant="h5">
            Register
          </Typography>
          <form className={classes.form} onSubmit={register}>
            <TextField
              required
              id="name"
              name="name"
              label="Name"
              type="text"
              autoComplete="name"
              value={name}
              onChange={(e) => setName(e.target.value)}
            />
            <TextField
              required
              id="email"
              name="email"
              label="Email Address"
              type="email"
              autoComplete="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
            <TextField
              required
              id="password"
              name="password"
              label="Password"
              type="password"
              autoComplete="new-password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
            <Button
              type="submit"
              fullWidth
              variant="contained"
              color="primary"
              className={classes.submit}
            >
              Register
            </Button>
            <Grid container justify="flex-end">
              <Grid item>
                Already have an account? <Link to="/" variant="body2">Sign in</Link>
              </Grid>
            </Grid>
          </form>
        </div>
      </Grid>
    </Grid>
  )
}

export default Register;
