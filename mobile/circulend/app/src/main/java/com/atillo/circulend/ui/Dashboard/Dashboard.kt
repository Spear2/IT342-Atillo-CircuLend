package com.atillo.circulend.ui.Dashboard

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.atillo.circulend.R
import com.atillo.circulend.ui.auth.LoginActivity
import com.atillo.circulend.ui.auth.RegisterActivity
import com.atillo.circulend.util.SessionManager

class Dashboard : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var logoutButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)

        sessionManager = SessionManager(this)

        logoutButton = findViewById(R.id.logoutButton)

        logoutButton.setOnClickListener{
            sessionManager.clear()
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}