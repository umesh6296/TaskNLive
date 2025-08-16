package com.example.tasknlive

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.tasknlive.databinding.ActivityMainBinding
import com.example.tasknlive.ui.tasks.TasksFragment
import com.google.android.material.navigation.NavigationBarView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.topAppBar)

        // Initialize with TasksFragment if no saved state
        if (savedInstanceState == null) {
            replaceFragment(TasksFragment())
        }

        setupBottomNavigation()
        setupFloatingActionButton()
    }

    private fun setupBottomNavigation() {
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_tasks -> replaceFragment(TasksFragment())
                R.id.nav_live_wall -> startActivity(Intent(this, LiveWallActivity::class.java))
                else -> return@setOnItemSelectedListener false
            }
            true
        }
    }

    private fun setupFloatingActionButton() {
        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, AddTaskActivity::class.java).apply {
                // Optional animation
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
            })
        }

        // Adjust FAB position when bottom nav is shown
        binding.bottomNav.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            binding.fabAdd.translationY = -binding.bottomNav.height.toFloat()
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.fragment_container, fragment)
            // Optional: Add to back stack if needed
            // addToBackStack(null)
        }
    }
}