package com.humana.store.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.humana.store.R
import com.humana.store.data.model.Store
import com.humana.store.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    private var googleMap: GoogleMap? = null
    private val promotionsAdapter = PromotionsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMap()
        setupRecyclerView()
        observeViewModel()
        viewModel.loadPromotions()
    }

    private fun setupMap() {
        try {
            Log.d("HomeFragment", "Setting up map...")
            val mapFragment = childFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync(this)
            Log.d("HomeFragment", "Map setup completed")
        } catch (e: Exception) {
            Log.e("HomeFragment", "Error setting up map", e)
        }
    }

    private fun setupRecyclerView() {
        binding.promotionsRecyclerView.apply {
            adapter = promotionsAdapter
            setHasFixedSize(true)
            addItemDecoration(androidx.recyclerview.widget.DividerItemDecoration(
                context,
                androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
            ))
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.stores.observe(viewLifecycleOwner) { stores ->
                updateMapMarkers(stores)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.promotions.observe(viewLifecycleOwner) { promotions ->
                promotionsAdapter.submitList(promotions)
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        try {
            Log.d("HomeFragment", "Map is ready")
            googleMap = map

            // Set default map settings
            map.uiSettings.apply {
                isZoomControlsEnabled = true
                isCompassEnabled = true
                isMyLocationButtonEnabled = true
            }

            // Add demo markers
            val demoLocations = listOf(
                Triple("თბილისი მოლი", "სააკაძის მოედანი 2", LatLng(41.7174, 44.7828)),
                Triple("ისთ ფოინთი", "კახეთის გზატკეცილი 2", LatLng(41.7028, 44.8315)),
                Triple("გალერია თბილისი", "რუსთაველის გამზირი 2/4", LatLng(41.7016, 44.7959)),
                Triple("სითი მოლი საბურთალო", "ვაჟა-ფშაველას გამზირი 45", LatLng(41.7276, 44.7536))
            )

            demoLocations.forEach { (title, address, latLng) ->
                map.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title(title)
                        .snippet(address)
                )
            }

            // Center map on Tbilisi center
            map.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(41.7151, 44.8271),
                    12f
                )
            )
            Log.d("HomeFragment", "Demo markers added and map centered")

            // Load promotions
            viewModel.loadPromotions()

        } catch (e: Exception) {
            Log.e("HomeFragment", "Error in onMapReady", e)
        }
    }

    private fun updateMapMarkers(stores: List<Store>) {
        try {
            Log.d("HomeFragment", "Updating map markers with ${stores.size} stores")
            // Commenting out the real data markers for now
            /*
            googleMap?.clear()
            stores.forEach { store ->
                googleMap?.addMarker(
                    MarkerOptions()
                        .position(LatLng(store.latitude, store.longitude))
                        .title(store.name)
                        .snippet(store.address)
                )
            }

            stores.firstOrNull()?.let { store ->
                googleMap?.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(store.latitude, store.longitude),
                        15f
                    )
                )
                Log.d("HomeFragment", "Map centered on store: ${store.name}")
            }
            */
        } catch (e: Exception) {
            Log.e("HomeFragment", "Error updating map markers", e)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
