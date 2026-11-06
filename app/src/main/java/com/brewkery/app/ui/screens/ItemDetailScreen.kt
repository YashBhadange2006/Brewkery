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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.brewkery.app.data.model.MilkOption
import com.brewkery.app.data.model.formatPrice
import com.brewkery.app.data.model.optionExtraPrice
import com.brewkery.app.ui.components.LoadingOrError
import com.brewkery.app.ui.components.CartButton
import com.brewkery.app.ui.components.OptionButton
import com.brewkery.app.ui.components.PageHeader
import com.brewkery.app.ui.components.PrimaryAction
import com.brewkery.app.ui.components.QuantityControl
import com.brewkery.app.ui.components.SectionHeading
import com.brewkery.app.ui.theme.BorderWarm
import com.brewkery.app.ui.theme.Cream
import com.brewkery.app.ui.theme.Espresso
import com.brewkery.app.ui.theme.Latte
import com.brewkery.app.ui.theme.Mocha
import com.brewkery.app.ui.theme.SurfaceWhite
import com.brewkery.app.ui.theme.Terracotta
import com.brewkery.app.viewmodel.CartViewModel
import com.brewkery.app.viewmodel.ItemState
import com.brewkery.app.viewmodel.MenuViewModel
import kotlinx.coroutines.launch

@Composable
fun ItemDetailScreen(
    itemId: Int,
    menuViewModel: MenuViewModel,
    cartViewModel: CartViewModel,
    onBack: () -> Unit,
    onOpenCart: () -> Unit
) {
    val state by menuViewModel.itemState.collectAsStateWithLifecycle()
    val menuState by menuViewModel.uiState.collectAsStateWithLifecycle()
    val cartState by cartViewModel.uiState.collectAsStateWithLifecycle()
    val currency = (menuState as? com.brewkery.app.viewmodel.UiState.Success)?.data?.meta?.currencySymbol ?: "$"
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    LaunchedEffect(itemId) { menuViewModel.loadItem(itemId) }

    Scaffold(containerColor = Cream, snackbarHost = { SnackbarHost(snackbar) }) { insets ->
        when (val current = state) {
            ItemState.Idle, ItemState.Loading -> Box(Modifier.fillMaxSize().padding(insets), contentAlignment = Alignment.Center) {
                LoadingOrError("", loading = true)
            }
            is ItemState.Error -> Column(Modifier.fillMaxSize().padding(insets)) {
                PageHeader("ITEM CUSTOMIZER", onBack = onBack)
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    LoadingOrError(current.message, loading = false, onRetry = { menuViewModel.loadItem(itemId) })
                }
            }
            is ItemState.Success -> if (current.item.id != itemId) {
                Box(Modifier.fillMaxSize().padding(insets), contentAlignment = Alignment.Center) { LoadingOrError("", loading = true) }
            } else {
                val item = current.item
                val customizations = item.customizations
                var selectedSizeId by rememberSaveable(item.id) { mutableStateOf(customizations?.sizes?.firstOrNull()?.id.orEmpty()) }
                var selectedMilkId by rememberSaveable(item.id) { mutableStateOf(customizations?.milkOptions?.firstOrNull()?.id.orEmpty()) }
                var selectedSugar by rememberSaveable(item.id) { mutableStateOf(customizations?.sugarLevels?.firstOrNull().orEmpty()) }
                var quantity by rememberSaveable(item.id) { mutableIntStateOf(1) }
                val selectedSize = customizations?.sizes?.firstOrNull { it.id == selectedSizeId }
                val selectedMilk = customizations?.milkOptions?.firstOrNull { it.id == selectedMilkId }
                val unitPrice = item.basePrice + (selectedSize?.extraPrice ?: 0.0) +
                    (selectedMilk?.extraPrice ?: 0.0) + optionExtraPrice(selectedSugar)

                Column(Modifier.fillMaxSize().padding(insets)) {
                    PageHeader("ITEM CUSTOMIZER", onBack = onBack, action = {
                        CartButton(cartState.itemCount, onOpenCart)
                    })
                    Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
                        Box {
                            AsyncImage(model = item.imageUrl, contentDescription = item.name, contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxWidth().height(205.dp).clip(RoundedCornerShape(20.dp)).background(Latte))
                            if (item.badge.isNotBlank()) Surface(Modifier.align(Alignment.TopStart).padding(10.dp), color = Espresso, shape = RoundedCornerShape(8.dp)) {
                                Text(item.badge, Modifier.padding(horizontal = 10.dp, vertical = 6.dp), color = androidx.compose.ui.graphics.Color(0xFFFFBA36), fontWeight = FontWeight.Bold, fontSize = 10.sp)
                            }
                        }
                        Row(Modifier.fillMaxWidth().padding(top = 13.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(item.name, Modifier.weight(1f), color = Espresso, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                            Text(formatPrice(item.basePrice, currency), color = Terracotta, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        Text(item.description, Modifier.padding(top = 5.dp), color = Mocha, fontSize = 12.sp, lineHeight = 18.sp)
                        Text("★ ${item.rating}  (${item.reviewCount} reviews)   ·   ${item.prepTime}   ·   ${item.calories} cal",
                            Modifier.padding(top = 8.dp), color = Mocha, fontSize = 11.sp)

                        if (item.ingredients.isNotEmpty()) {
                            SectionHeading("KEY INGREDIENTS", Modifier.padding(top = 16.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(item.ingredients, key = { it }) { ingredient ->
                                    Surface(color = SurfaceWhite, shape = RoundedCornerShape(8.dp), border = BorderStroke(1.dp, BorderWarm)) {
                                        Text(ingredient, Modifier.padding(horizontal = 9.dp, vertical = 6.dp), color = Mocha, fontSize = 10.sp)
                                    }
                                }
                            }
                        }

                        if (!customizations?.sizes.isNullOrEmpty()) {
                            CustomizationCard("Size Selection") {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    customizations!!.sizes.forEach { option ->
                                        OptionButton(
                                            "${option.label}\n+${formatPrice(option.extraPrice, currency)}",
                                            selectedSizeId == option.id,
                                            { selectedSizeId = option.id },
                                            Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }
                        if (!customizations?.milkOptions.isNullOrEmpty()) {
                            CustomizationCard("Milk Options / Spreads") {
                                Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                                    customizations!!.milkOptions.forEach { option ->
                                        MilkChoice(option, selectedMilkId == option.id, currency) { selectedMilkId = option.id }
                                    }
                                }
                            }
                        }
                        if (!customizations?.sugarLevels.isNullOrEmpty()) {
                            CustomizationCard("Sugar Levels / Serving") {
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                                    items(customizations!!.sugarLevels, key = { it }) { option ->
                                        OptionButton(option, selectedSugar == option, { selectedSugar = option })
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                    }
                    Row(Modifier.fillMaxWidth().background(Cream).padding(horizontal = 20.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        QuantityControl(quantity, { quantity = (quantity + it).coerceAtLeast(1) }, compact = true)
                        PrimaryAction("Add to Cart  •  ${formatPrice(unitPrice * quantity, currency)}", onClick = {
                            cartViewModel.addItem(CartItem(item, selectedSize, selectedMilk, selectedSugar.ifBlank { null }, quantity))
                            scope.launch { snackbar.showSnackbar("${item.name} added to your cart") }
                        }, modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun CustomizationCard(title: String, content: @Composable () -> Unit) {
    Surface(Modifier.fillMaxWidth().padding(top = 12.dp), color = SurfaceWhite, shape = RoundedCornerShape(16.dp), border = BorderStroke(1.dp, BorderWarm)) {
        Column(Modifier.padding(11.dp)) {
            Text(title, color = Espresso, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Spacer(Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
private fun MilkChoice(option: MilkOption, selected: Boolean, currency: String, onClick: () -> Unit) {
    Surface(Modifier.fillMaxWidth().clickable(onClick = onClick), color = if (selected) Latte else SurfaceWhite,
        shape = RoundedCornerShape(10.dp), border = BorderStroke(1.dp, if (selected) Terracotta else BorderWarm)) {
        Row(Modifier.padding(horizontal = 10.dp, vertical = 9.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(option.name, Modifier.weight(1f), color = if (selected) Terracotta else Mocha, fontSize = 12.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal)
            Text("+${formatPrice(option.extraPrice, currency)}", color = if (selected) Terracotta else Mocha, fontSize = 10.sp)
        }
    }
}
