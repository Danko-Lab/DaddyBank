package com.example.daddybank

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var bankDataRepository: BankDataRepository
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bankDataRepository = BankDataRepository(this)

        // Fetch users and handle the result
        coroutineScope.launch {
            val result = bankDataRepository.fetchUsers()
            if (result.isSuccess) {
                // If fetching users is successful, add LoginFragment
                addLoginFragment(supportFragmentManager)
            } else {
                // Handle error, e.g., show an error message or try again
                // depending on your app requirements
            }
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
            replace(R.id.fragment_container, HistoryFragment.newInstance())
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
