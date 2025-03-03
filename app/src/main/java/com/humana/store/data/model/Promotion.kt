package com.humana.store.data.model

data class Promotion(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val type: PromotionType = PromotionType.DISCOUNT,
    val discountPercentage: Int = 0,
    val fixedPrice: Double = 0.0,
    val startDate: Long = 0,
    val endDate: Long = 0,
    val storeId: String = "",
    val category: String = "",
    val imageUrl: String = ""
)
