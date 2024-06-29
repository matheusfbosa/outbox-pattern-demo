package com.github.matheusfbosa.order

import kotlinx.serialization.Serializable

@Serializable
data class CreateOrderRequestDTO(
    val customerId: String,
    val item: String,
    val quantity: Int,
    val totalPrice: Int
)

@Serializable
data class CreateOrderResponseDTO(
    val orderId: String
)
