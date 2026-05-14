package com.atillo.circulend.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.atillo.circulend.R
import com.atillo.circulend.ui.borrower.DashboardFragment
import com.atillo.circulend.ui.borrower.ExploreItemsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainContainerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_container)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)

        if (savedInstanceState == null) {
            showFragment(ExploreItemsFragment()) // default tab
            bottomNav.selectedItemId = R.id.nav_explore
        }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_explore -> {
                    showFragment(ExploreItemsFragment())
                    true
                }
                R.id.nav_dashboard -> {
                    showFragment(DashboardFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_fragment_container, fragment)
            .commit()
    }
}