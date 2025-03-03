package com.humana.store.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.humana.store.data.model.Store
import com.humana.store.data.repository.StoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManageStoresViewModel @Inject constructor(
    private val storeRepository: StoreRepository
) : ViewModel() {

    private val _stores = MutableStateFlow<List<Store>>(emptyList())
    val stores: StateFlow<List<Store>> = _stores

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadStores()
    }

    fun loadStores() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            storeRepository.getAllStores()
                .catch { e ->
                    _error.value = e.message
                    _isLoading.value = false
                }
                .collect { storesList ->
                    _stores.value = storesList
                    _isLoading.value = false
                }
        }
    }

    fun addStore(store: Store) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            storeRepository.addStore(store)
                .onSuccess {
                    loadStores()
                }
                .onFailure { e ->
                    _error.value = e.message
                    _isLoading.value = false
                }
        }
    }

    fun updateStore(store: Store) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            storeRepository.updateStore(store)
                .onSuccess {
                    loadStores()
                }
                .onFailure { e ->
                    _error.value = e.message
                    _isLoading.value = false
                }
        }
    }

    fun deleteStore(storeId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            storeRepository.deleteStore(storeId)
                .onSuccess {
                    loadStores()
                }
                .onFailure { e ->
                    _error.value = e.message
                    _isLoading.value = false
                }
        }
    }
}
