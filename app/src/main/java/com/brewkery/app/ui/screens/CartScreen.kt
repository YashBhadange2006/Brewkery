package com.brewkery.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.brewkery.app.data.model.CartItem
import com.brewkery.app.data.model.CartPricing
import com.brewkery.app.data.model.MenuMeta
import com.brewkery.app.data.model.formatPrice
import com.brewkery.app.ui.components.PageHeader
import com.brewkery.app.ui.components.PrimaryAction
import com.brewkery.app.ui.components.QuantityControl
import com.brewkery.app.ui.theme.BorderWarm
import com.brewkery.app.ui.theme.Cream
import com.brewkery.app.ui.theme.Espresso
import com.brewkery.app.ui.theme.Latte
import com.brewkery.app.ui.theme.Mocha
import com.brewkery.app.ui.theme.SurfaceWhite
import com.brewkery.app.ui.theme.Terracotta
import com.brewkery.app.viewmodel.CartViewModel
import com.brewkery.app.viewmodel.MenuViewModel
import com.brewkery.app.viewmodel.UiState

@Composable
fun CartScreen(menuViewModel: MenuViewModel, cartViewModel: CartViewModel, onBack: () -> Unit, onPlaceOrder: () -> Unit) {
    val cart by cartViewModel.uiState.collectAsStateWithLifecycle()
    val menu by menuViewModel.uiState.collectAsStateWithLifecycle()
    val meta = (menu as? UiState.Success)?.data?.meta ?: MenuMeta()
    val pricing = CartPricing.calculate(cart.items, meta.deliveryFee, meta.taxRatePercent)
    var confirmClear by remember { mutableStateOf(false) }

    if (confirmClear) AlertDialog(onDismissRequest = { confirmClear = false },
        title = { Text("Clear your cart?") }, text = { Text("All items will be removed from this cart.") },
        confirmButton = { TextButton(onClick = { cartViewModel.clearCart(); confirmClear = false }) { Text("Clear cart", color = Terracotta) } },
        dismissButton = { TextButton(onClick = { confirmClear = false }) { Text("Keep items") } })

    Scaffold(containerColor = Cream) { insets ->
        Column(Modifier.fillMaxSize().padding(insets)) {
            PageHeader("YOUR CART", onBack = onBack, action = {
                if (cart.items.isNotEmpty()) Text("Clear", Modifier.clickable { confirmClear = true }.padding(5.dp), color = Terracotta, fontSize = 12.sp)
            })
            if (cart.items.isEmpty()) {
                Box(Modifier.weight(1f).fillMaxWidth().padding(horizontal = 20.dp), contentAlignment = Alignment.TopCenter) {
                    Surface(Modifier.fillMaxWidth().padding(top = 4.dp), color = SurfaceWhite, shape = RoundedCornerShape(16.dp), border = BorderStroke(1.dp, BorderWarm)) {
                        Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🛒", fontSize = 30.sp)
                            Text("Your cart is empty.", Modifier.padding(top = 5.dp), color = Mocha, fontSize = 13.sp)
                        }
                    }
                }
            } else {
                LazyColumn(Modifier.weight(1f).padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 8.dp)) {
                    items(cart.items, key = { it.configurationKey }) { item ->
                        CartItemCard(item, meta.currencySymbol,
                            onQuantityChange = { delta -> cartViewModel.changeQuantity(item.configurationKey, delta) },
                            onRemove = { cartViewModel.removeItem(item.configurationKey) })
                    }
                }
            }
            Column(Modifier.fillMaxWidth().background(Cream).padding(horizontal = 20.dp, vertical = 12.dp)) {
                Surface(color = SurfaceWhite, shape = RoundedCornerShape(17.dp), border = BorderStroke(1.dp, BorderWarm)) {
                    Column(Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
                        PriceRow("Subtotal", formatPrice(pricing.subtotal, meta.currencySymbol))
                        PriceRow("Delivery Fee", formatPrice(pricing.deliveryFee, meta.currencySymbol))
                        PriceRow("Est. Tax (${meta.taxRatePercent}%)", formatPrice(pricing.tax, meta.currencySymbol))
                        Spacer(Modifier.height(6.dp))
                        androidx.compose.material3.HorizontalDivider(color = BorderWarm)
                        PriceRow("Total Payable", formatPrice(if (cart.items.isEmpty()) 0.0 else pricing.total, meta.currencySymbol), bold = true, color = Terracotta)
                    }
                }
                val payable = if (cart.items.isEmpty()) 0.0 else pricing.total
                PrimaryAction("Place Order Now  •  ${formatPrice(payable, meta.currencySymbol)}", onClick = {
                    if (cartViewModel.placeOrder(meta) != null) onPlaceOrder()
                }, modifier = Modifier.fillMaxWidth().padding(top = 12.dp), enabled = cart.items.isNotEmpty())
            }
        }
    }
}

@Composable
private fun PriceRow(label: String, value: String, bold: Boolean = false, color: androidx.compose.ui.graphics.Color = Espresso) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = if (bold) Espresso else Mocha, fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal, fontSize = if (bold) 14.sp else 12.sp)
        Text(value, color = color, fontWeight = FontWeight.Bold, fontSize = if (bold) 16.sp else 12.sp)
    }
}

@Composable
private fun CartItemCard(item: CartItem, currency: String, onQuantityChange: (Int) -> Unit, onRemove: () -> Unit) {
    Surface(Modifier.fillMaxWidth(), color = SurfaceWhite, shape = RoundedCornerShape(16.dp), border = BorderStroke(1.dp, BorderWarm)) {
        Row(Modifier.padding(11.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(model = item.item.imageUrl, contentDescription = item.item.name, contentScale = ContentScale.Crop,
                modifier = Modifier.size(68.dp).clip(RoundedCornerShape(12.dp)).background(Latte))
            Column(Modifier.weight(1f).padding(start = 10.dp)) {
                Text(item.item.name, color = Espresso, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(listOfNotNull(item.selectedSize?.label, item.selectedMilk?.name, item.selectedSugar).joinToString(" · "),
                    Modifier.padding(top = 2.dp), color = Mocha, fontSize = 10.sp, maxLines = 2)
                Text("Each ${formatPrice(item.unitPrice, currency)}", Modifier.padding(top = 3.dp), color = Mocha, fontSize = 10.sp)
                Row(Modifier.fillMaxWidth().padding(top = 7.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    QuantityControl(item.quantity, onQuantityChange, compact = true)
                    Text(formatPrice(item.totalPrice, currency), color = Terracotta, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
            Text("×", Modifier.clickable(onClick = onRemove).padding(start = 8.dp, bottom = 38.dp), color = Mocha, fontSize = 18.sp)
        }
    }
}
