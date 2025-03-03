package com.humana.store.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import com.humana.store.R
import com.humana.store.data.repository.PromotionRepository
import com.humana.store.data.repository.StoreRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LocationService : Service() {
    @Inject
    lateinit var fusedLocationClient: FusedLocationProviderClient

    @Inject
    lateinit var storeRepository: StoreRepository

    @Inject
    lateinit var promotionRepository: PromotionRepository

    @Inject
    lateinit var notificationService: NotificationService

    private lateinit var locationCallback: LocationCallback
    private val CHANNEL_ID = "location_service_channel"
    private val NOTIFICATION_ID = 1
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var lastNotifiedStoreId: String? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
        setupLocationCallback()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startLocationUpdates()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Location Service Channel",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    private fun createNotification() = NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle("ჰუმანა")
        .setContentText("მაღაზიის მახლობლად ყოფნის მონიტორინგი")
        .setSmallIcon(R.drawable.ic_notification)
        .build()

    private fun setupLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    serviceScope.launch {
                        checkNearbyStores(location.latitude, location.longitude)
                    }
                }
            }
        }
    }

    private suspend fun checkNearbyStores(latitude: Double, longitude: Double) {
        val radiusInKm = 0.2 // 200 meters
        val nearbyStores = storeRepository.getNearbyStores(latitude, longitude, radiusInKm)

        nearbyStores.forEach { store ->
            if (store.id != lastNotifiedStoreId) {
                notificationService.showStoreNearbyNotification(store)
                lastNotifiedStoreId = store.id

                // Check for active promotions
                val promotions = promotionRepository.getStorePromotions(store.id)
                promotions.forEach { promotion ->
                    notificationService.showPromotionNotification(promotion, store)
                }
            }
        }
    }

    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
            .setMinUpdateDistanceMeters(10f)
            .build()

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            // Handle permission not granted
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}
