package com.example.fyp_mobile

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth


class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val loginButton = findViewById<Button>(R.id.loginButton)
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        auth = FirebaseAuth.getInstance()

        loginButton.setOnClickListener {

            progressBar.visibility = View.VISIBLE

            if(emailEditText.text.toString().isBlank()){
                emailEditText.requestFocus()
                emailEditText.error = "Field cannot be left empty"
                return@setOnClickListener
            }

            else if(passwordEditText.text.isBlank()){
                passwordEditText.requestFocus()
                passwordEditText.error = "Field cannot be left empty"
                return@setOnClickListener
            }

            else {
                if (!emailEditText.text.toString().isValidEmail()) {
                    emailEditText.requestFocus()
                    emailEditText.error = "Enter a valid email"
                    return@setOnClickListener
                }
                //Field validation is complete
                else {

                    auth.signInWithEmailAndPassword(emailEditText.text.toString(), passwordEditText.text.toString())
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                progressBar.visibility = View.GONE
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success")
                                val user = auth.currentUser

                                val intent = Intent(this, CameraActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.exception)
                                Toast.makeText(baseContext, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
        }
    }
    private fun String.isValidEmail() =
        !TextUtils.isEmpty(this) && Patterns.EMAIL_ADDRESS.matcher(this).matches()
}