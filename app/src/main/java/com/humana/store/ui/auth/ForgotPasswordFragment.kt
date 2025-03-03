package com.humana.store.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.humana.store.databinding.FragmentForgotPasswordBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ForgotPasswordFragment : Fragment() {
    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        observeAuthState()
    }

    private fun setupClickListeners() {
        binding.btnResetPassword.setOnClickListener {
            val email = binding.emailInput.text.toString()

            if (email.isEmpty()) {
                Toast.makeText(requireContext(), "გთხოვთ შეიყვანოთ ელ-ფოსტა", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.resetPassword(email)
            }
        }

        binding.loginText.setOnClickListener {
            findNavController().navigate(ForgotPasswordFragmentDirections.actionForgotPasswordFragmentToLoginFragment())
        }
    }

    private fun observeAuthState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.authState.observe(viewLifecycleOwner) { state ->
                when (state) {
                    is AuthState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        setInputsEnabled(false)
                    }
                    is AuthState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        setInputsEnabled(true)
                        Toast.makeText(
                            requireContext(),
                            "პაროლის აღდგენის ინსტრუქცია გამოგზავნილია თქვენს ელ-ფოსტაზე",
                            Toast.LENGTH_LONG
                        ).show()
                        findNavController().navigate(ForgotPasswordFragmentDirections.actionForgotPasswordFragmentToLoginFragment())
                    }
                    is AuthState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        setInputsEnabled(true)
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun setInputsEnabled(enabled: Boolean) {
        binding.emailInput.isEnabled = enabled
        binding.btnResetPassword.isEnabled = enabled
        binding.loginText.isEnabled = enabled
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
