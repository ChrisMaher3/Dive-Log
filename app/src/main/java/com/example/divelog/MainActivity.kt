package com.example.divelog

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Ensure this points to your correct layout file
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.bottom_nav_menu, menu) // Inflate the menu
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_diver_planner -> {
                // Handle Diver Planner action
                true
            }
            R.id.action_my_dives -> {
                // Handle My Dives action
                true
            }
            R.id.action_add_dive -> {
                // Handle Add Dive action
                true
            }
            R.id.action_profile -> {
                // Handle Profile action
                true
            }
            R.id.action_settings -> {
                // Handle Settings action
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
