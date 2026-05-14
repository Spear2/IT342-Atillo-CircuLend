package com.atillo.circulend.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.atillo.circulend.R
import com.atillo.circulend.data.repository.AuthResult
import com.atillo.circulend.data.repository.UserRepository
import com.atillo.circulend.ui.auth.LoginActivity
import com.atillo.circulend.util.SessionManager

class Dashboard : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var logoutButton: Button
    private val userRepository = UserRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        sessionManager = SessionManager(this)


        // Protected endpoint test: GET /api/user/me
        userRepository.me { result ->
            runOnUiThread {
                when (result) {
                    is AuthResult.Success -> {
                        val user = result.data
                        Toast.makeText(
                            this,
                            "Hi ${user.firstName} (${user.role})",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    is AuthResult.Error -> {
                        if (result.message.equals("UNAUTHORIZED", ignoreCase = true)) {
                            sessionManager.clear()
                            startActivity(Intent(this, LoginActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, result.message, Toast.LENGTH_LONG).show()
                            // stay in Dashboard
                        }
                    }
                }
            }
        }


    }
}