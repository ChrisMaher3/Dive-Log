package com.example.divelog

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Load the default fragment (AddDiveFragment) when the activity starts
        if (savedInstanceState == null) {
            loadFragment(AddDiveFragment()) // Load the AddDiveFragment as the default
        }

        // Set up the BottomNavigationView
        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_add_dive -> {
                    loadFragment(AddDiveFragment()) // Load the AddDiveFragment when selected
                    true
                }
                R.id.action_diver_planner -> {
                    loadFragment(DiverPlannerFragment()) // Load the DiverPlannerFragment
                    true
                }
                R.id.action_my_dives -> {
                    loadFragment(MyDivesFragment()) // Load the MyDivesFragment
                    true
                }
                R.id.action_profile -> {
                    loadFragment(ProfileFragment()) // Load the ProfileFragment
                    true
                }
                R.id.action_settings -> {
                    loadFragment(SettingsFragment()) // Load the SettingsFragment
                    true
                }
                else -> false
            }
        }
    }

    // Inflate the menu for the bottom navigation (not used here anymore)
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }

    // Function to load a specified fragment into the fragment container
    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment) // Replace the existing fragment
        transaction.addToBackStack(null) // Optional: Add this transaction to the back stack for navigation
        transaction.commit() // Commit the transaction to apply changes
    }
}
