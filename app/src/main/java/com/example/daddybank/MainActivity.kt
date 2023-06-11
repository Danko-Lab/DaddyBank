package com.example.daddybank

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import kotlinx.coroutines.*
import androidx.activity.viewModels
import android.app.AlertDialog

class MainActivity : AppCompatActivity() {
    private lateinit var bankDataRepository: BankDataRepository
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private val sharedViewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bankDataRepository = BankDataRepository(this)

        // Show a loading indicator here

        // Fetch users and handle the result
        coroutineScope.launch {
            bankDataRepository.loadData()
            val users = bankDataRepository.users
            if (users.isNotEmpty()) {
                // If loading users is successful, add LoginFragment
                addLoginFragment(supportFragmentManager)
            } else {
                // Handle error, e.g., show an error message or try again
                AlertDialog.Builder(this@MainActivity)
                    .setTitle("Error")
                    .setMessage("No users found. Exiting...")
                    .setPositiveButton("OK") { _, _ ->
                        finish()
                    }
                    .create()
                    .show()
            }

            // Hide the loading indicator here
        }
    }

    private fun addLoginFragment(fragmentManager: FragmentManager) {
        fragmentManager.commit {
            replace(R.id.fragment_container, LoginFragment.newInstance(bankDataRepository))
            addToBackStack(null)
        }
    }

    fun navigateToDashboard() {
        val fragmentManager = supportFragmentManager
        fragmentManager.commit {
            replace(R.id.fragment_container, DashboardFragment.newInstance())
            addToBackStack(null)
        }
    }

    fun navigateToHistory() {
        val fragmentManager = supportFragmentManager
        fragmentManager.commit {
            replace(R.id.fragment_container, HistoryFragment.newInstance(bankDataRepository))
            addToBackStack(null)
        }
    }

    fun navigateToProjections() {
        val fragmentManager = supportFragmentManager
        fragmentManager.commit {
            replace(R.id.fragment_container, ProjectionsFragment.newInstance())
            addToBackStack(null)
        }
    }

    fun navigateToExplore() {
        val fragmentManager = supportFragmentManager
        fragmentManager.commit {
            replace(R.id.fragment_container, ExploreFragment.newInstance())
            addToBackStack(null)
        }
    }
}
