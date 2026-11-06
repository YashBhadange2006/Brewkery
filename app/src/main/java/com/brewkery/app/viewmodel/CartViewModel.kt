package com.brewkery.app.viewmodel

import androidx.lifecycle.ViewModel
import com.brewkery.app.data.model.CartItem
import com.brewkery.app.data.model.CartPricing
import com.brewkery.app.data.model.MenuMeta
import com.brewkery.app.data.model.Order
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

data class CartUiState(
    val items: List<CartItem> = emptyList(),
    val activeOrder: Order? = null
) {
    val itemCount: Int get() = items.sumOf { it.quantity }
}

class CartViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    fun addItem(item: CartItem) {
        val current = _uiState.value
        val existing = current.items.indexOfFirst { it.configurationKey == item.configurationKey }
        val updated = if (existing < 0) current.items + item else current.items.mapIndexed { index, cartItem ->
            if (index == existing) cartItem.copy(quantity = cartItem.quantity + item.quantity) else cartItem
        }
        _uiState.value = current.copy(items = updated)
    }

    fun changeQuantity(configurationKey: String, delta: Int) {
        _uiState.value = _uiState.value.copy(items = _uiState.value.items.mapNotNull { item ->
            if (item.configurationKey != configurationKey) item
            else item.copy(quantity = item.quantity + delta).takeIf { it.quantity > 0 }
        })
    }

    fun removeItem(configurationKey: String) {
        _uiState.value = _uiState.value.copy(items = _uiState.value.items.filterNot { it.configurationKey == configurationKey })
    }

    fun clearCart() { _uiState.value = _uiState.value.copy(items = emptyList()) }

    fun placeOrder(meta: MenuMeta): Order? {
        val state = _uiState.value
        if (state.items.isEmpty()) return null
        val pricing = CartPricing.calculate(state.items, meta.deliveryFee, meta.taxRatePercent)
        val order = Order(
            ticketId = "BK-${UUID.randomUUID().toString().take(6).uppercase()}",
            items = state.items.toList(),
            subtotal = pricing.subtotal,
            deliveryFee = pricing.deliveryFee,
            tax = pricing.tax,
            total = pricing.total,
            estimatedTime = meta.estimatedDeliveryTime,
            currencySymbol = meta.currencySymbol
        )
        _uiState.value = CartUiState(activeOrder = order)
        return order
    }
}
