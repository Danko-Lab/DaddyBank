package com.example.daddybank

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import android.util.Log
import androidx.fragment.app.viewModels

class DashboardFragment : Fragment() {
    private val sharedViewModel: SharedViewModel by viewModels({ requireActivity() })

    private lateinit var balanceTextView: TextView
    private lateinit var historyButton: Button
    private lateinit var projectionsButton: Button
    private lateinit var exploreButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("DashboardFragment", "onViewCreated called")

        balanceTextView = view.findViewById(R.id.balance_text_view)
        historyButton = view.findViewById(R.id.history_button)
        projectionsButton = view.findViewById(R.id.projections_button)
        exploreButton = view.findViewById(R.id.explore_button)

        // Set the current principal balance (replace this with actual balance from dbstatefile.xml)
        val currentBalance = sharedViewModel.accountValuesSeries.value?.last()?.second ?: 0.0
        balanceTextView.text = getString(R.string.current_balance, currentBalance)

        historyButton.setOnClickListener {
            (activity as MainActivity).navigateToHistory()
        }

        projectionsButton.setOnClickListener {
            Log.d("DashboardFragment", "projectionsButton clicked")
            (activity as MainActivity).navigateToProjections()
        }

        exploreButton.setOnClickListener {
            (activity as MainActivity).navigateToExplore()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = DashboardFragment()
    }
}
