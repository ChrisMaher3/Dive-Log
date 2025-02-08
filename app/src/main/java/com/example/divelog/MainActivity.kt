package com.chris.divelog

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var diveRepository: DiveRepository // Declare the DiveRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        diveRepository = DiveRepository(this) // Initialize the repository

        // Load the default fragment (ProfileFragment) when the activity starts
        if (savedInstanceState == null) {
            loadFragment(ProfileFragment()) // Load the ProfileFragment as the default
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
                    loadFragment(MyDivesFragment.newInstance(diveRepository.getAllDives())) // Load MyDivesFragment with dives from the database
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
    fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment) // Replace the existing fragment
        transaction.addToBackStack(null) // Optional: Add this transaction to the back stack for navigation
        transaction.commit() // Commit the transaction to apply changes
    }

    // Method to add a dive to the database
    fun addDive(dive: Dive) {
        diveRepository.addDive(dive) // Save the dive to the database
    }

}