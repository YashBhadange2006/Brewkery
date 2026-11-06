package com.brewkery.app.data.model

import com.google.gson.annotations.SerializedName
import java.util.Locale

data class MenuResponse(
    val meta: MenuMeta = MenuMeta(),
    val categories: List<Category> = emptyList(),
    val items: List<MenuItem> = emptyList()
)

data class MenuMeta(
    val app: String = "Brewkery",
    val version: String = "1.0.0",
    val tagline: String = "Artisanal Coffee & Fresh Oven Bakes",
    val currency: String = "USD",
    @SerializedName("currency_symbol") val currencySymbol: String = "$",
    @SerializedName("delivery_fee") val deliveryFee: Double = 2.50,
    @SerializedName("tax_rate_percent") val taxRatePercent: Double = 8.0,
    @SerializedName("estimated_delivery_time") val estimatedDeliveryTime: String = "20 - 30 mins"
)

data class Category(
    val id: String = "",
    val name: String = "",
    val icon: String = "",
    @SerializedName("item_count") val itemCount: Int = 0
)

data class MenuItem(
    val id: Int = 0,
    @SerializedName("category_id") val categoryId: String = "",
    val name: String = "",
    val tagline: String = "",
    val description: String = "",
    @SerializedName("base_price") val basePrice: Double = 0.0,
    val rating: Double = 0.0,
    @SerializedName("review_count") val reviewCount: Int = 0,
    @SerializedName("prep_time") val prepTime: String = "",
    val calories: Int = 0,
    @SerializedName("image_url") val imageUrl: String = "",
    val badge: String = "",
    val ingredients: List<String> = emptyList(),
    val customizations: Customizations? = null
)

data class Customizations(
    val sizes: List<SizeOption> = emptyList(),
    @SerializedName("sugar_levels") val sugarLevels: List<String> = emptyList(),
    @SerializedName("milk_options") val milkOptions: List<MilkOption> = emptyList()
)

data class SizeOption(
    val id: String = "",
    val label: String = "",
    @SerializedName("extra_price") val extraPrice: Double = 0.0
)

data class MilkOption(
    val id: String = "",
    val name: String = "",
    @SerializedName("extra_price") val extraPrice: Double = 0.0
)

data class CartItem(
    val item: MenuItem,
    val selectedSize: SizeOption?,
    val selectedMilk: MilkOption?,
    val selectedSugar: String?,
    val quantity: Int
) {
    val sugarExtraPrice: Double get() = optionExtraPrice(selectedSugar.orEmpty())
    val unitPrice: Double get() = item.basePrice + (selectedSize?.extraPrice ?: 0.0) +
        (selectedMilk?.extraPrice ?: 0.0) + sugarExtraPrice
    val totalPrice: Double get() = unitPrice * quantity

    val configurationKey: String
        get() = listOf(item.id, selectedSize?.id.orEmpty(), selectedMilk?.id.orEmpty(), selectedSugar.orEmpty())
            .joinToString("|")
}

data class Order(
    val ticketId: String,
    val items: List<CartItem>,
    val subtotal: Double,
    val deliveryFee: Double,
    val tax: Double,
    val total: Double,
    val status: String = "PREPARING",
    val estimatedTime: String = "20 - 30 mins",
    val currencySymbol: String = "$"
)

data class CartPricingSummary(
    val subtotal: Double,
    val deliveryFee: Double,
    val tax: Double,
    val total: Double
)

object CartPricing {
    fun calculate(items: List<CartItem>, deliveryFee: Double, taxRatePercent: Double): CartPricingSummary {
        val subtotal = items.sumOf { it.totalPrice }
        val tax = subtotal * taxRatePercent / 100.0
        return CartPricingSummary(subtotal, deliveryFee, tax, subtotal + deliveryFee + tax)
    }
}

fun optionExtraPrice(label: String): Double {
    // Nitro Cherry Cascara encodes its honey add-on price in the sugar option label.
    val amount = Regex("\\(\\s*\\+\\s*\\$?([0-9]+(?:\\.[0-9]{1,2})?)\\s*\\)")
        .find(label)?.groupValues?.getOrNull(1)?.toDoubleOrNull()
    return amount ?: 0.0
}

fun formatPrice(amount: Double, currencySymbol: String): String =
    "$currencySymbol${String.format(Locale.US, "%.2f", amount)}"
