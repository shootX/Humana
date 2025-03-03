package com.humana.store.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.humana.store.data.model.Promotion
import com.humana.store.data.model.PromotionType
import com.humana.store.data.model.Store
import com.humana.store.data.repository.PromotionRepository
import com.humana.store.data.repository.StoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val storeRepository: StoreRepository,
    private val promotionRepository: PromotionRepository
) : ViewModel() {

    private val _stores = MutableLiveData<List<Store>>()
    val stores: LiveData<List<Store>> = _stores

    private val _promotions = MutableLiveData<List<Promotion>>()
    val promotions: LiveData<List<Promotion>> = _promotions

    fun loadStores() {
        viewModelScope.launch {
            try {
                Log.d("HomeViewModel", "Loading stores...")
                val storesList = storeRepository.getStores()
                Log.d("HomeViewModel", "Loaded stores: ${storesList.size}")
                _stores.value = storesList
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error loading stores", e)
            }
        }
    }

    fun loadPromotions() {
        viewModelScope.launch {
            try {
                Log.d("HomeViewModel", "Loading promotions...")
                // Using demo promotions instead of real data
                val demoPromotions = listOf(
                    Promotion(
                        id = "promo1",
                        title = "ზაფხულის ფასდაკლება",
                        description = "20% ფასდაკლება ყველა საზაფხულო ტანსაცმელზე",
                        type = PromotionType.DISCOUNT,
                        discountPercentage = 20,
                        startDate = System.currentTimeMillis(),
                        endDate = System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000, // 7 days
                        storeId = "store1",
                        category = "ტანსაცმელი",
                        imageUrl = "https://via.placeholder.com/300"
                    ),
                    Promotion(
                        id = "promo2",
                        title = "1+1 აქცია",
                        description = "იყიდე ერთი, მიიღე მეორე უფასოდ",
                        type = PromotionType.FIXED_PRICE,
                        fixedPrice = 0.0,
                        startDate = System.currentTimeMillis(),
                        endDate = System.currentTimeMillis() + 3 * 24 * 60 * 60 * 1000, // 3 days
                        storeId = "store2",
                        category = "სპორტი",
                        imageUrl = "https://via.placeholder.com/300"
                    ),
                    Promotion(
                        id = "promo3",
                        title = "გახსნის აქცია",
                        description = "ახალი მაღაზიის გახსნის აღსანიშნავად 30% ფასდაკლება",
                        type = PromotionType.OPENING,
                        discountPercentage = 30,
                        startDate = System.currentTimeMillis(),
                        endDate = System.currentTimeMillis() + 5 * 24 * 60 * 60 * 1000, // 5 days
                        storeId = "store3",
                        category = "ყველა",
                        imageUrl = "https://via.placeholder.com/300"
                    )
                )
                _promotions.value = demoPromotions
                Log.d("HomeViewModel", "Loaded demo promotions: ${demoPromotions.size}")
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error loading promotions", e)
            }
        }
    }
}
