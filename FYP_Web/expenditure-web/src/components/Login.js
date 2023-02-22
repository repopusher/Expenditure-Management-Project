import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { auth, logInWithEmailAndPassword } from '../config/firebase';
import { useAuthState } from 'react-firebase-hooks/auth';

function Login() {

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [user, loading, error] = useAuthState(auth);
  
  const navigate = useNavigate();

  //Track auth state of user
	useEffect(() => {

		if (loading) {
		  // maybe trigger a loading screen
		  return;
		}
		//If user already authenticated then redirect to dashboard
		if (user) navigate("/dashboard");
	
	}, [user, loading, navigate]);
  
  return (
    <div className="login">
      <div className="login__container">

        <input
          type="text"
          className="login__textBox"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          placeholder="Email"
        />

        <input
          type="password"
          className="login__textBox"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          placeholder="Password"
        />

        <button
          className="login__btn"
          onClick={() => logInWithEmailAndPassword(email, password)}
        >
            Login 
        </button>

        <div>
          <Link to="/reset">Forgot Password?</Link>
        </div>

        <div>
          Don't have an account? <Link to="/register">Register</Link> now.
        </div>

      </div>
    </div>
  )
}

export default Login;
