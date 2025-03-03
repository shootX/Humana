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
import com.humana.store.databinding.FragmentRegisterBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        observeAuthState()
    }

    private fun setupClickListeners() {
        binding.btnRegister.setOnClickListener {
            val name = binding.nameInput.text.toString()
            val email = binding.emailInput.text.toString()
            val password = binding.passwordInput.text.toString()
            val confirmPassword = binding.confirmPasswordInput.text.toString()

            when {
                name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() -> {
                    Toast.makeText(requireContext(), "გთხოვთ შეავსოთ ყველა ველი", Toast.LENGTH_SHORT).show()
                }
                password != confirmPassword -> {
                    Toast.makeText(requireContext(), "პაროლები არ ემთხვევა", Toast.LENGTH_SHORT).show()
                }
                password.length < 6 -> {
                    Toast.makeText(requireContext(), "პაროლი უნდა შეიცავდეს მინიმუმ 6 სიმბოლოს", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    viewModel.signUp(email, password, name)
                }
            }
        }

        binding.loginText.setOnClickListener {
            findNavController().popBackStack()
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
                        findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToHomeFragment())
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
        binding.nameInput.isEnabled = enabled
        binding.emailInput.isEnabled = enabled
        binding.passwordInput.isEnabled = enabled
        binding.confirmPasswordInput.isEnabled = enabled
        binding.btnRegister.isEnabled = enabled
        binding.loginText.isEnabled = enabled
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
