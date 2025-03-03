package com.humana.store.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.humana.store.data.model.Store
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StoreRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    fun getAllStores(): Flow<List<Store>> = flow {
        try {
            val snapshot = firestore.collection("stores")
                .get()
                .await()

            val stores = snapshot.documents.map { doc ->
                Store(
                    id = doc.id,
                    name = doc.getString("name") ?: "",
                    address = doc.getString("address") ?: "",
                    latitude = doc.getDouble("latitude") ?: 0.0,
                    longitude = doc.getDouble("longitude") ?: 0.0,
                    phoneNumber = doc.getString("phoneNumber") ?: "",
                    workingHours = doc.getString("workingHours") ?: "",
                    imageUrl = doc.getString("imageUrl") ?: ""
                )
            }
            emit(stores)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    suspend fun addStore(store: Store): Result<Unit> {
        return try {
            val storeData = hashMapOf(
                "name" to store.name,
                "address" to store.address,
                "latitude" to store.latitude,
                "longitude" to store.longitude,
                "phoneNumber" to store.phoneNumber,
                "workingHours" to store.workingHours,
                "imageUrl" to store.imageUrl
            )

            firestore.collection("stores")
                .document()
                .set(storeData)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateStore(store: Store): Result<Unit> {
        return try {
            val storeData = hashMapOf(
                "name" to store.name,
                "address" to store.address,
                "latitude" to store.latitude,
                "longitude" to store.longitude,
                "phoneNumber" to store.phoneNumber,
                "workingHours" to store.workingHours,
                "imageUrl" to store.imageUrl
            )

            firestore.collection("stores")
                .document(store.id)
                .set(storeData)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteStore(storeId: String): Result<Unit> {
        return try {
            firestore.collection("stores")
                .document(storeId)
                .delete()
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getStores(): List<Store> {
        return try {
            firestore.collection("stores")
                .get()
                .await()
                .toObjects(Store::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getNearbyStores(latitude: Double, longitude: Double, radiusInKm: Double): List<Store> {
        return try {
            val stores = getStores()
            stores.filter { store ->
                calculateDistance(latitude, longitude, store.latitude, store.longitude) <= radiusInKm
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun calculateDistance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        val r = 6371 // Earth's radius in kilometers
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = kotlin.math.sin(dLat / 2) * kotlin.math.sin(dLat / 2) +
                kotlin.math.cos(Math.toRadians(lat1)) * kotlin.math.cos(Math.toRadians(lat2)) *
                kotlin.math.sin(dLon / 2) * kotlin.math.sin(dLon / 2)
        val c = 2 * kotlin.math.atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1 - a))
        return r * c
    }
}
