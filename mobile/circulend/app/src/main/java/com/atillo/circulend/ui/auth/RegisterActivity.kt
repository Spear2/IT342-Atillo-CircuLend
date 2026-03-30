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
import com.atillo.circulend.data.model.request.RegisterRequest
import com.atillo.circulend.data.model.response.ApiResponse
import com.atillo.circulend.data.model.response.LoginData
import com.atillo.circulend.data.remote.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var firstNameInput: EditText
    private lateinit var lastNameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var confirmPasswordInput: EditText
    private lateinit var createAccountButton: Button
    private lateinit var signInText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        // these IDs already exist in your registration XML
        firstNameInput = findViewById(R.id.firstNameInput)
        lastNameInput = findViewById(R.id.lastNameInput)
        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput)
        createAccountButton = findViewById(R.id.createAccountButton)
        signInText = findViewById(R.id.signInText)

        createAccountButton.setOnClickListener { attemptRegister() }

        signInText.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun attemptRegister() {
        val firstName = firstNameInput.text.toString().trim()
        val lastName = lastNameInput.text.toString().trim()
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString().trim()
        val confirmPassword = confirmPasswordInput.text.toString().trim()

        when {
            firstName.isEmpty() -> {
                firstNameInput.error = "First name required"
                firstNameInput.requestFocus()
                return
            }
            lastName.isEmpty() -> {
                lastNameInput.error = "Last name required"
                lastNameInput.requestFocus()
                return
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                emailInput.error = "Invalid email"
                emailInput.requestFocus()
                return
            }
            password.length < 8 -> {
                passwordInput.error = "Password must be at least 8 characters"
                passwordInput.requestFocus()
                return
            }
            password != confirmPassword -> {
                confirmPasswordInput.error = "Passwords do not match"
                confirmPasswordInput.requestFocus()
                return
            }
        }

        createAccountButton.isEnabled = false

        val request = RegisterRequest(firstName, lastName, email, password)
        RetrofitClient.authApi.register(request).enqueue(object : Callback<ApiResponse<LoginData>> {
            override fun onResponse(
                call: Call<ApiResponse<LoginData>>,
                response: Response<ApiResponse<LoginData>>
            ) {
                createAccountButton.isEnabled = true

                val body = response.body()
                if (response.isSuccessful && body?.success == true) {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Registration successful. Please login.",
                        Toast.LENGTH_LONG
                    ).show()
                    startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                    finish()
                } else {
                    val msg = body?.error?.message ?: "Registration failed."
                    Toast.makeText(this@RegisterActivity, msg, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<LoginData>>, t: Throwable) {
                createAccountButton.isEnabled = true
                Toast.makeText(
                    this@RegisterActivity,
                    "Network error: ${t.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }
}