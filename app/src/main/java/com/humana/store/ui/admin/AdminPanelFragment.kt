package com.humana.store.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.humana.store.databinding.FragmentAdminPanelBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminPanelFragment : Fragment() {
    private var _binding: FragmentAdminPanelBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AdminViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminPanelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnManageStores.setOnClickListener {
            findNavController().navigate(AdminPanelFragmentDirections.actionAdminPanelFragmentToManageStoresFragment())
        }

        binding.btnManagePromotions.setOnClickListener {
            findNavController().navigate(AdminPanelFragmentDirections.actionAdminPanelFragmentToManagePromotionsFragment())
        }

        binding.btnManageUsers.setOnClickListener {
            findNavController().navigate(AdminPanelFragmentDirections.actionAdminPanelFragmentToManageUsersFragment())
        }

        binding.btnStatistics.setOnClickListener {
            findNavController().navigate(AdminPanelFragmentDirections.actionAdminPanelFragmentToStatisticsFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
