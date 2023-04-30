package com.example.daddybank

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.fragment.app.Fragment

class LoginFragment : Fragment() {

    private lateinit var userSpinner: Spinner
    private lateinit var loginButton: Button
    private lateinit var bankDataRepository: BankDataRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bankDataRepository = arguments?.getSerializable(BANK_DATA_REPOSITORY) as BankDataRepository
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userSpinner = view.findViewById(R.id.user_spinner)
        loginButton = view.findViewById(R.id.login_button)

        // Fetch users from BankDataRepository
        val users = bankDataRepository.getCachedUsers().map { it.name }

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, users)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        userSpinner.adapter = adapter

        loginButton.setOnClickListener {
            // Add logic to load user data based on the selected user
            (activity as MainActivity).navigateToDashboard()
        }
    }

    companion object {
        private const val BANK_DATA_REPOSITORY = "bank_data_repository"

        @JvmStatic
        fun newInstance(bankDataRepository: BankDataRepository) = LoginFragment().apply {
            arguments = Bundle().apply {
                putSerializable(BANK_DATA_REPOSITORY, bankDataRepository)
            }
        }
    }
}
