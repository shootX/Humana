package com.humana.store.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.humana.store.data.model.Promotion
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PromotionRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun getActivePromotions(): List<Promotion> {
        return try {
            val currentTime = System.currentTimeMillis()
            firestore.collection("promotions")
                .whereGreaterThanOrEqualTo("startDate", currentTime)
                .whereLessThanOrEqualTo("endDate", currentTime)
                .get()
                .await()
                .toObjects(Promotion::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getStorePromotions(storeId: String): List<Promotion> {
        return try {
            val currentTime = System.currentTimeMillis()
            firestore.collection("promotions")
                .whereEqualTo("storeId", storeId)
                .whereGreaterThanOrEqualTo("startDate", currentTime)
                .whereLessThanOrEqualTo("endDate", currentTime)
                .get()
                .await()
                .toObjects(Promotion::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getCategoryPromotions(category: String): List<Promotion> {
        return try {
            val currentTime = System.currentTimeMillis()
            firestore.collection("promotions")
                .whereEqualTo("category", category)
                .whereGreaterThanOrEqualTo("startDate", currentTime)
                .whereLessThanOrEqualTo("endDate", currentTime)
                .get()
                .await()
                .toObjects(Promotion::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
