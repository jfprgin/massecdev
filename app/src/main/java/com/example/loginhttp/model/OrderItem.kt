package com.example.loginhttp.model

sealed class OrderItem {
    abstract val id: Int
    abstract val timestamp: String
    abstract val status: OrderStatus

    data class InternalOrder(
        override val id: Int,
        override val timestamp: String,
        override val status: OrderStatus,
        val fromLocation: String,
        val toLocation: String,
    ) : OrderItem()

    data class ExternalOrder(
        override val id: Int,
        override val timestamp: String,
        override val status: OrderStatus,
        val supplier: String,
        val warehouse: String
    ) : OrderItem()
}

enum class OrderStatus {
    U_PROCESU,
    ZATVORENO
}

enum class OrderType {
    INTERNAL,
    EXTERNAL
}