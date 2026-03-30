package com.atillo.circulend.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.atillo.circulend.R
import com.atillo.circulend.data.model.request.LoginRequest
import com.atillo.circulend.data.model.response.ApiResponse
import com.atillo.circulend.data.model.response.LoginData
import com.atillo.circulend.data.remote.RetrofitClient
import com.atillo.circulend.ui.Dashboard.Dashboard
import com.atillo.circulend.util.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    // Change these IDs based on your XML
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var goToRegisterText: TextView

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sessionManager = SessionManager(this)

        emailInput = findViewById(R.id.emailAddress) // <-- adjust if needed
        passwordInput = findViewById(R.id.enteredPassword) // <-- adjust if needed
        loginButton = findViewById(R.id.loginButton) // <-- adjust if needed
        goToRegisterText = findViewById(R.id.goToRegisterText) // <-- adjust if needed

        loginButton.setOnClickListener { attemptLogin() }

        goToRegisterText.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun attemptLogin() {
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in email and password.", Toast.LENGTH_SHORT).show()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.error = "Invalid email format"
            emailInput.requestFocus()
            return
        }

        loginButton.isEnabled = false

        val request = LoginRequest(email, password)
        RetrofitClient.authApi.login(request).enqueue(object : Callback<ApiResponse<LoginData>> {
            override fun onResponse(
                call: Call<ApiResponse<LoginData>>,
                response: Response<ApiResponse<LoginData>>
            ) {
                loginButton.isEnabled = true

                val body = response.body()
                if (response.isSuccessful && body?.success == true && body.data != null) {
                    val token = body.data.accessToken
                    val role = body.data.user.role

                    sessionManager.saveToken(token)
                    sessionManager.saveRole(role)

                    Toast.makeText(this@LoginActivity, "Login successful", Toast.LENGTH_SHORT).show()

                    // Replace with your next activity
                     startActivity(Intent(this@LoginActivity, Dashboard::class.java))
                     finish()
                } else {
                    val msg = body?.error?.message ?: "Invalid email or password."
                    Toast.makeText(this@LoginActivity, msg, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<LoginData>>, t: Throwable) {
                loginButton.isEnabled = true
                Toast.makeText(
                    this@LoginActivity,
                    "Network error: ${t.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }
}