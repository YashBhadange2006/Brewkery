package com.brewkery.app.data.model

import com.google.gson.annotations.SerializedName


data class MenuResponse(
    val meta: MenuMeta,
    val categories: List<Category>,
    val items: List<MenuItem>
)


data class MenuMeta(
    val app: String,
    val version: String,
    val tagline: String,
    val currency: String,
    @SerializedName("currency_symbol")
    val currencySymbol: String,
    @SerializedName("delivery_fee")
    val deliveryFee: Double,
    @SerializedName("tax_rate_percent")
    val taxRatePercent: Double,
    @SerializedName("estimated_delivery_time")
    val estimatedDeliveryTime: String
)


data class Category(
    val id: String,
    val name: String,
    val icon: String,
    @SerializedName("item_count")
    val itemCount: Int
)


data class MenuItem(
    val id: Int,
    @SerializedName("category_id")
    val categoryId: String,
    val name: String,
    val tagline: String,
    val description: String,
    @SerializedName("base_price")
    val basePrice: Double,
    val rating: Double,
    @SerializedName("review_count")
    val reviewCount: Int,
    @SerializedName("prep_time")
    val prepTime: String,
    val calories: Int,
    @SerializedName("image_url")
    val imageUrl: String,
    val badge: String,
    val ingredients: List<String>,
    val customizations: Customizations
)

data class Customizations(
    val sizes: List<SizeOption>,
    @SerializedName("sugar_levels")
    val sugarLevels: List<String>,
    @SerializedName("milk_options")
    val milkOptions: List<MilkOption>
)

data class SizeOption(
    val id: String,
    val label: String,
    @SerializedName("extra_price")
    val extraPrice: Double
)

data class MilkOption(
    val id: String,
    val name: String,
    @SerializedName("extra_price")
    val extraPrice: Double
)

data class CartItem(
    val item: MenuItem,
    val selectedSize: SizeOption,
    val selectedMilk: MilkOption,
    val selectedSugar: String,
    val quantity: Int
) {
    val unitPrice: Double
        get() = item.basePrice +
                selectedSize.extraPrice +
                selectedMilk.extraPrice

    val totalPrice: Double
        get() = unitPrice * quantity
}

data class Order(
    val ticketId: String,
    val items: List<CartItem>,
    val subtotal: Double,
    val deliveryFee: Double,
    val tax: Double,
    val total: Double,
    val status: String = "PREPARING",
    val estimatedTime: String = "10 - 15 mins"
)
