import React, { useEffect, useState } from 'react';
import { useAuthState } from 'react-firebase-hooks/auth';
import { Link, useNavigate } from 'react-router-dom';
import { auth, sendPasswordReset } from '../config/firebase';
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

function Reset() {

  const [email, setEmail] = useState("");
  const [user, loading] = useAuthState(auth);
  
  const navigate = useNavigate();
  const classes = useStyles();

  const resetPassword = (e) => {
    e.preventDefault();
    sendPasswordReset(email);
    setEmail("");
    alert("Password reset email sent successfully");
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
            Reset Password
          </Typography>
          <form className={classes.form} onSubmit={resetPassword}>
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
            <Button
              type="submit"
              fullWidth
              variant="contained"
              color="primary"
              className={classes.submit}
            >
              Send password reset email
            </Button>
            <Grid container spacing={1} justify="flex-end">
              <Grid item>
                Don't have an account? <Link to="/register" variant="body2">Register</Link>
              </Grid>
              <Grid item>
                Back to <Link to="/" variant="body2">Login</Link>
              </Grid>
            </Grid>
          </form>
        </div>
      </Grid>
    </Grid>
  )
}

export default Reset;
