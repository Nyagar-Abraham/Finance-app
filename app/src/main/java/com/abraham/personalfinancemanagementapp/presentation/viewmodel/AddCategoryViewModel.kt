package com.abraham.personalfinancemanagementapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abraham.personalfinancemanagementapp.data.model.Category
import com.abraham.personalfinancemanagementapp.domain.repository.IAuthRepository
import com.abraham.personalfinancemanagementapp.domain.repository.ICategoryRepository
import com.abraham.personalfinancemanagementapp.util.Constants
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import java.util.UUID

/**
 * ViewModel for Add/Edit Category screen
 */
class AddCategoryViewModel(
    private val authRepository: IAuthRepository,
    private val categoryRepository: ICategoryRepository,
    private val categoryId: String? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow<AddCategoryUiState>(AddCategoryUiState.Idle)
    val uiState: StateFlow<AddCategoryUiState> = _uiState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    var name = MutableStateFlow("")
    var selectedType = MutableStateFlow(Constants.TRANSACTION_TYPE_EXPENSE)
    var icon = MutableStateFlow("💰")
    var color = MutableStateFlow("#4CAF50")

    init {
        if (categoryId != null) {
            loadCategory()
        }
    }

    private fun loadCategory() {
        viewModelScope.launch {
            if (categoryId != null) {
                _isLoading.value = true
                try {
                    val category = categoryRepository.getCategoryById(categoryId)
                    if (category != null) {
                        name.value = category.name
                        selectedType.value = category.type
                        icon.value = category.icon
                        color.value = category.color
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    _uiState.value = AddCategoryUiState.Error(e.message ?: "Failed to load category")
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }

    fun saveCategory() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = authRepository.getCurrentUser()
                if (user == null) {
                    _uiState.value = AddCategoryUiState.Error("User not logged in")
                    return@launch
                }

                if (name.value.isBlank()) {
                    _uiState.value = AddCategoryUiState.Error("Category name cannot be empty")
                    return@launch
                }

                val category = Category(
                    id = categoryId ?: UUID.randomUUID().toString(),
                    name = name.value.trim(),
                    icon = icon.value,
                    color = color.value,
                    isDefault = false,
                    userId = user.id,
                    type = selectedType.value
                )

                if (categoryId == null) {
                    categoryRepository.addCategory(category)
                } else {
                    categoryRepository.updateCategory(category)
                }

                _uiState.value = AddCategoryUiState.Success
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = AddCategoryUiState.Error(e.message ?: "Failed to save category")
            } finally {
                _isLoading.value = false
            }
        }
    }
}

sealed class AddCategoryUiState {
    data object Idle : AddCategoryUiState()
    data object Success : AddCategoryUiState()
    data class Error(val message: String) : AddCategoryUiState()
}

