package com.humana.store.data.model

data class Store(
    val id: String = "",
    val name: String = "",
    val address: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val phoneNumber: String = "",
    val workingHours: String = "",
    val imageUrl: String = ""
)
