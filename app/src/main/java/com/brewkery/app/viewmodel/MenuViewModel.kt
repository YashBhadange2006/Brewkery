package com.brewkery.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brewkery.app.data.model.MenuResponse
import com.brewkery.app.data.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


sealed interface UiState {
    object Loading : UiState
    data class Success(val data: MenuResponse) : UiState
    data class Error(val message: String) : UiState

}

class MenuViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    init {
        fetchMenu()
    }

    private fun fetchMenu() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getMenu()
                _uiState.value = UiState.Success(response)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "Unknown error")}
        }
    }
}