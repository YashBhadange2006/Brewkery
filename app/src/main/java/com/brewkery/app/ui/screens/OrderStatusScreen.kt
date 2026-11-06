package com.brewkery.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.brewkery.app.data.model.formatPrice
import com.brewkery.app.ui.components.PrimaryAction
import com.brewkery.app.ui.theme.BorderWarm
import com.brewkery.app.ui.theme.Cream
import com.brewkery.app.ui.theme.Espresso
import com.brewkery.app.ui.theme.Honey
import com.brewkery.app.ui.theme.Mocha
import com.brewkery.app.ui.theme.Sage
import com.brewkery.app.ui.theme.SurfaceWhite
import com.brewkery.app.ui.theme.Terracotta
import com.brewkery.app.viewmodel.CartViewModel

@Composable
fun OrderStatusScreen(cartViewModel: CartViewModel, onBackToMenu: () -> Unit) {
    val cart by cartViewModel.uiState.collectAsStateWithLifecycle()
    val order = cart.activeOrder
    Scaffold(containerColor = Cream) { insets ->
        if (order == null) {
            Column(Modifier.fillMaxSize().padding(insets).padding(24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                Text("No active order", color = Espresso, fontWeight = FontWeight.Bold)
                PrimaryAction("Back to Menu", onBackToMenu, Modifier.fillMaxWidth().padding(top = 18.dp))
            }
        } else {
            Column(Modifier.fillMaxSize().padding(insets).padding(horizontal = 20.dp, vertical = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(Modifier.height(22.dp))
                Surface(Modifier.padding(bottom = 12.dp), color = Cream, shape = CircleShape, border = BorderStroke(2.dp, Terracotta)) {
                    Box(Modifier.padding(18.dp), contentAlignment = Alignment.Center) { Text("☕", fontSize = 34.sp) }
                }
                Text("ORDER PLACED", color = Terracotta, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Text("Brewing in Progress!", Modifier.padding(top = 5.dp), color = Espresso, fontSize = 21.sp, fontWeight = FontWeight.Bold)
                Text("Your ticket is ready for the barista.", Modifier.padding(top = 4.dp), color = Mocha, fontSize = 13.sp)
                Spacer(Modifier.height(54.dp))
                Surface(Modifier.fillMaxWidth(), color = SurfaceWhite, shape = RoundedCornerShape(18.dp), border = BorderStroke(1.dp, BorderWarm)) {
                    Column(Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text("ORDER TICKET", color = Mocha, fontWeight = FontWeight.Medium, fontSize = 10.sp)
                                Text("#${order.ticketId}", Modifier.padding(top = 4.dp), color = Espresso, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                            Surface(color = Honey, shape = RoundedCornerShape(50)) {
                                Text(order.status, Modifier.padding(horizontal = 12.dp, vertical = 8.dp), color = Espresso, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                            }
                        }
                        androidx.compose.material3.HorizontalDivider(Modifier.padding(vertical = 12.dp), color = BorderWarm)
                        StatusRow("Estimated Wait", order.estimatedTime)
                        StatusRow("Items Ordered", "${order.items.sumOf { it.quantity }} item(s)")
                        StatusRow("Order Total", formatPrice(order.total, order.currencySymbol))
                        Spacer(Modifier.height(10.dp))
                        Text("Status:", color = Mocha, fontSize = 11.sp)
                        Text("Your order is saved on this device. The café has not confirmed it yet.", Modifier.padding(top = 3.dp), color = Sage, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
                Spacer(Modifier.weight(1f))
                PrimaryAction("Back to Menu", onBackToMenu, Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun StatusRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Mocha, fontSize = 12.sp)
        Text(value, color = Terracotta, fontSize = 12.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.End)
    }
}
