package com.brewkery.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brewkery.app.data.model.MenuItem
import com.brewkery.app.data.model.MenuResponse
import com.brewkery.app.data.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import java.util.Locale

sealed interface UiState {
    data object Loading : UiState
    data class Success(val data: MenuResponse) : UiState
    data class Error(val message: String) : UiState
}

sealed interface ItemState {
    data object Idle : ItemState
    data object Loading : ItemState
    data class Success(val item: MenuItem) : ItemState
    data class Error(val message: String) : ItemState
}

class MenuViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    private val _itemState = MutableStateFlow<ItemState>(ItemState.Idle)
    val itemState: StateFlow<ItemState> = _itemState.asStateFlow()
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    private val _selectedCategoryId = MutableStateFlow<String?>(null)
    val selectedCategoryId: StateFlow<String?> = _selectedCategoryId.asStateFlow()
    val filteredItems: StateFlow<List<MenuItem>> = combine(_uiState, _searchQuery, _selectedCategoryId) { state, query, categoryId ->
        val items = (state as? UiState.Success)?.data?.items.orEmpty()
        filterItems(items, query, categoryId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init { fetchMenu() }

    fun fetchMenu() {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            _uiState.value = try {
                UiState.Success(RetrofitInstance.api.getMenu())
            } catch (e: Exception) {
                UiState.Error(e.localizedMessage ?: "Couldn't load the menu. Check your connection and try again.")
            }
        }
    }

    fun loadItem(id: Int) {
        if ((_itemState.value as? ItemState.Success)?.item?.id == id) return
        _itemState.value = ItemState.Loading
        viewModelScope.launch {
            _itemState.value = try {
                ItemState.Success(RetrofitInstance.api.getItem(id))
            } catch (e: Exception) {
                ItemState.Error(e.localizedMessage ?: "Couldn't load this item. Please try again.")
            }
        }
    }

    fun setSearchQuery(query: String) { _searchQuery.value = query }
    fun selectCategory(categoryId: String?) { _selectedCategoryId.value = categoryId }

    private fun filterItems(items: List<MenuItem>, rawQuery: String, categoryId: String?): List<MenuItem> {
        val query = rawQuery.trim().lowercase(Locale.ROOT)
        return items.filter { item ->
            (categoryId == null || item.categoryId == categoryId) &&
                (query.isEmpty() || item.name.lowercase(Locale.ROOT).contains(query) || item.tagline.lowercase(Locale.ROOT).contains(query))
        }
    }
}
