package com.brewkery.app.ui.screens

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.brewkery.app.viewmodel.MenuViewModel
import com.brewkery.app.viewmodel.UiState

@Composable
fun HomeScreen(viewModel: MenuViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold{ paddingValues ->
        when (val currentState = state)
        {
            is UiState.Loading -> {
                CircularProgressIndicator()
            }

            is UiState.Error -> {
                Text(text ="Error: ${currentState.message}")
            }
            is UiState.Success -> {
                val items = currentState.data.items

                LazyColumn(contentPadding = paddingValues) {
                    items(items) { item ->
                        ListItem(
                            headlineContent = {Text(item.name)},
                            supportingContent = { Text(item.tagline) },
                            trailingContent = { Text("$${item.basePrice}") }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}