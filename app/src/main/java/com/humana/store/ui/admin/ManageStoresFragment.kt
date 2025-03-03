package com.humana.store.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.humana.store.data.model.Store
import com.humana.store.databinding.DialogStoreBinding
import com.humana.store.databinding.FragmentManageStoresBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ManageStoresFragment : Fragment() {
    private var _binding: FragmentManageStoresBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ManageStoresViewModel by viewModels()
    private lateinit var storesAdapter: StoresAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentManageStoresBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupClickListeners()
        observeStores()
        observeLoading()
        observeError()
    }

    private fun setupRecyclerView() {
        storesAdapter = StoresAdapter(
            onEditClick = { store ->
                showEditStoreDialog(store)
            },
            onDeleteClick = { store ->
                showDeleteConfirmationDialog(store)
            }
        )

        binding.storesRecyclerView.apply {
            adapter = storesAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    private fun setupClickListeners() {
        binding.fabAddStore.setOnClickListener {
            showAddStoreDialog()
        }

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun observeStores() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.stores.collectLatest { stores ->
                storesAdapter.submitList(stores)
                binding.emptyView.visibility = if (stores.isEmpty()) View.VISIBLE else View.GONE
            }
        }
    }

    private fun observeLoading() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collectLatest { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }
    }

    private fun observeError() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.error.collectLatest { error ->
                error?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showAddStoreDialog() {
        val dialogBinding = DialogStoreBinding.inflate(layoutInflater)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("ახალი მაღაზიის დამატება")
            .setView(dialogBinding.root)
            .setPositiveButton("დამატება") { dialog, _ ->
                val store = Store(
                    name = dialogBinding.nameInput.text.toString(),
                    address = dialogBinding.addressInput.text.toString(),
                    latitude = dialogBinding.latitudeInput.text.toString().toDoubleOrNull() ?: 0.0,
                    longitude = dialogBinding.longitudeInput.text.toString().toDoubleOrNull() ?: 0.0,
                    phoneNumber = dialogBinding.phoneInput.text.toString(),
                    workingHours = dialogBinding.workingHoursInput.text.toString(),
                    imageUrl = dialogBinding.imageUrlInput.text.toString()
                )
                viewModel.addStore(store)
                dialog.dismiss()
            }
            .setNegativeButton("გაუქმება") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showEditStoreDialog(store: Store) {
        val dialogBinding = DialogStoreBinding.inflate(layoutInflater)

        // პრე-ფილინგი არსებული მონაცემებით
        dialogBinding.apply {
            nameInput.setText(store.name)
            addressInput.setText(store.address)
            latitudeInput.setText(store.latitude.toString())
            longitudeInput.setText(store.longitude.toString())
            phoneInput.setText(store.phoneNumber)
            workingHoursInput.setText(store.workingHours)
            imageUrlInput.setText(store.imageUrl)
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("მაღაზიის რედაქტირება")
            .setView(dialogBinding.root)
            .setPositiveButton("განახლება") { dialog, _ ->
                val updatedStore = store.copy(
                    name = dialogBinding.nameInput.text.toString(),
                    address = dialogBinding.addressInput.text.toString(),
                    latitude = dialogBinding.latitudeInput.text.toString().toDoubleOrNull() ?: 0.0,
                    longitude = dialogBinding.longitudeInput.text.toString().toDoubleOrNull() ?: 0.0,
                    phoneNumber = dialogBinding.phoneInput.text.toString(),
                    workingHours = dialogBinding.workingHoursInput.text.toString(),
                    imageUrl = dialogBinding.imageUrlInput.text.toString()
                )
                viewModel.updateStore(updatedStore)
                dialog.dismiss()
            }
            .setNegativeButton("გაუქმება") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showDeleteConfirmationDialog(store: Store) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("მაღაზიის წაშლა")
            .setMessage("ნამდვილად გსურთ მაღაზიის წაშლა?")
            .setPositiveButton("წაშლა") { dialog, _ ->
                viewModel.deleteStore(store.id)
                dialog.dismiss()
            }
            .setNegativeButton("გაუქმება") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
