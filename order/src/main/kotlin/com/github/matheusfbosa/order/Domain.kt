package com.github.matheusfbosa.order

import kotlinx.serialization.Serializable

enum class OrderStatus {
    CREATED
}

@Serializable
data class Order(
    val orderId: String,
    val customerId: String,
    val item: String,
    val quantity: Int,
    val totalPrice: Int,
    val status: OrderStatus,
    val orderDate: String
)
