package com.brewkery.app.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.brewkery.app.data.model.formatPrice
import com.brewkery.app.ui.components.CartButton
import com.brewkery.app.ui.components.CategoryChip
import com.brewkery.app.ui.components.LoadingOrError
import com.brewkery.app.ui.components.MenuProductCard
import com.brewkery.app.ui.components.SearchBox
import com.brewkery.app.ui.theme.BorderWarm
import com.brewkery.app.ui.theme.Cream
import com.brewkery.app.ui.theme.Espresso
import com.brewkery.app.ui.theme.Latte
import com.brewkery.app.ui.theme.Mocha
import com.brewkery.app.ui.theme.Sage
import com.brewkery.app.ui.theme.SurfaceWhite
import com.brewkery.app.ui.theme.Terracotta
import com.brewkery.app.viewmodel.CartViewModel
import com.brewkery.app.viewmodel.MenuViewModel
import com.brewkery.app.viewmodel.UiState

@Composable
fun HomeScreen(
    menuViewModel: MenuViewModel,
    cartViewModel: CartViewModel,
    onOpenItem: (Int) -> Unit,
    onOpenCart: () -> Unit,
    onOpenOrder: () -> Unit
) {
    val menuState by menuViewModel.uiState.collectAsStateWithLifecycle()
    val cartState by cartViewModel.uiState.collectAsStateWithLifecycle()
    val query by menuViewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by menuViewModel.selectedCategoryId.collectAsStateWithLifecycle()
    val visibleItems by menuViewModel.filteredItems.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    LaunchedEffect(query, selectedCategory) {
        if (listState.firstVisibleItemIndex > 0) listState.scrollToItem(0)
    }

    Scaffold(containerColor = Cream) { insets ->
        Column(Modifier.fillMaxSize().padding(insets)) {
            Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Surface(color = Espresso, shape = CircleShape, modifier = Modifier.size(38.dp)) {
                    Box(contentAlignment = Alignment.Center) { Text("BK", color = SurfaceWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                }
                Column(Modifier.weight(1f).padding(start = 8.dp)) {
                    Text("Fresh Roast & Bakes", color = Mocha, fontSize = 11.sp)
                    Text("Brewkery Artisans", color = Espresso, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                CartButton(cartState.itemCount, onOpenCart)
            }

            when (val state = menuState) {
                UiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { LoadingOrError("", loading = true) }
                is UiState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    LoadingOrError(state.message, loading = false, onRetry = menuViewModel::fetchMenu)
                }
                is UiState.Success -> {
                    val menu = state.data
                    LazyColumn(state = listState, modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        item(key = "banner") {
                            if (cartState.activeOrder != null) ActiveOrderCard(
                                ticket = cartState.activeOrder!!.ticketId,
                                estimated = cartState.activeOrder!!.estimatedTime,
                                onClick = onOpenOrder
                            ) else StoreBanner(estimatedTime = menu.meta.estimatedDeliveryTime, deliveryFee = formatPrice(menu.meta.deliveryFee, menu.meta.currencySymbol))
                        }
                        item(key = "search") { SearchBox(query, menuViewModel::setSearchQuery, Modifier.padding(horizontal = 20.dp)) }
                        item(key = "categories") {
                            LazyRow(contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                item(key = "all") { CategoryChip("All Items", selectedCategory == null) { menuViewModel.selectCategory(null) } }
                                items(menu.categories, key = { it.id }) { category ->
                                    CategoryChip("${category.icon} ${category.name}", selectedCategory == category.id) { menuViewModel.selectCategory(category.id) }
                                }
                            }
                        }
                        if (visibleItems.isEmpty()) {
                            item(key = "empty") {
                                Column(Modifier.fillMaxWidth().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("☕", fontSize = 34.sp)
                                    Text(
                                        when {
                                            query.isNotBlank() -> "No matches for ‘${query.trim()}’"
                                            selectedCategory != null -> "No items in this category yet"
                                            else -> "The menu is empty right now"
                                        },
                                        color = Espresso,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    )
                                    if (query.isNotBlank() || selectedCategory != null) {
                                        TextButton(onClick = {
                                            menuViewModel.setSearchQuery("")
                                            menuViewModel.selectCategory(null)
                                        }) { Text("Clear filters", color = Terracotta) }
                                    }
                                }
                            }
                        } else {
                            items(visibleItems, key = { it.id }) { item ->
                                MenuProductCard(item, menu.meta.currencySymbol) { onOpenItem(item.id) }
                            }
                        }
                        item(key = "bottom-space") { Spacer(Modifier.height(12.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun StoreBanner(estimatedTime: String, deliveryFee: String) {
    Surface(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp), color = Latte,
        shape = RoundedCornerShape(16.dp), border = BorderStroke(1.dp, BorderWarm)) {
        Row(Modifier.padding(horizontal = 14.dp, vertical = 13.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("☕", fontSize = 23.sp)
            Column(Modifier.weight(1f).padding(start = 10.dp)) {
                Text("FRESH FROM OUR OVEN", color = Terracotta, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                Text("Made with care, delivered warm", color = Espresso, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            }
            Text("$estimatedTime\n$deliveryFee delivery", color = Mocha, fontSize = 10.sp, textAlign = TextAlign.End)
        }
    }
}

@Composable
private fun ActiveOrderCard(ticket: String, estimated: String, onClick: () -> Unit) {
    Surface(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp).clickable(onClick = onClick),
        color = Latte, shape = RoundedCornerShape(17.dp), border = BorderStroke(1.dp, BorderWarm)) {
        Row(Modifier.padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(color = SurfaceWhite, shape = RoundedCornerShape(12.dp), modifier = Modifier.size(40.dp)) {
                Box(contentAlignment = Alignment.Center) { Text("●", color = Sage, fontSize = 19.sp) }
            }
            Column(Modifier.weight(1f).padding(start = 10.dp)) {
                Text("ACTIVE ORDER  •", color = Sage, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                Text("Active Order #$ticket", color = Espresso, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text("Preparing (Arriving in $estimated)", color = Mocha, fontSize = 10.sp)
            }
            Surface(color = Sage, shape = RoundedCornerShape(9.dp)) { Text("Track", Modifier.padding(horizontal = 13.dp, vertical = 8.dp), color = SurfaceWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp) }
        }
    }
}
