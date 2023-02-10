package com.example.fyp_mobile

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity


class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val loginButton = findViewById<Button>(R.id.loginButton)
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)

        loginButton.setOnClickListener {
            if(emailEditText.text.toString().isBlank()){
                emailEditText.requestFocus()
                emailEditText.error = "Field cannot be left empty"
            }

            else if(passwordEditText.text.isBlank()){
                passwordEditText.requestFocus()
                passwordEditText.error = "Field cannot be left empty"
            }

            else {
                if (!emailEditText.text.toString().isValidEmail()) {
                    emailEditText.requestFocus()
                    emailEditText.error = "Enter a valid email"
                }
                else {
//                    TODO AUTHENTICATE LOGIN WHEN DB CONNECTION IS IMPLEMENTED
                    val intent = Intent(this, CameraActivity::class.java)
//                    intent.putExtra("key", value)
                    startActivity(intent)
                }
            }
        }
    }
    private fun String.isValidEmail() =
        !TextUtils.isEmpty(this) && Patterns.EMAIL_ADDRESS.matcher(this).matches()
}