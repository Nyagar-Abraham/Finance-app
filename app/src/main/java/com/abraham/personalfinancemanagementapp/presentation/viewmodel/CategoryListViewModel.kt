package com.abraham.personalfinancemanagementapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abraham.personalfinancemanagementapp.domain.repository.IAuthRepository
import com.abraham.personalfinancemanagementapp.domain.repository.ICategoryRepository
import com.abraham.personalfinancemanagementapp.util.Constants
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel for Category List screen
 */
class CategoryListViewModel(
    private val authRepository: IAuthRepository,
    private val categoryRepository: ICategoryRepository
) : ViewModel() {

    private val _categories = MutableStateFlow<List<com.abraham.personalfinancemanagementapp.data.model.Category>>(emptyList())
    val categories: StateFlow<List<com.abraham.personalfinancemanagementapp.data.model.Category>> = _categories.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val selectedFilter = MutableStateFlow("all") // "all", "income", "expense"

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            
            val user = authRepository.getCurrentUser()
            if (user != null) {
                when (selectedFilter.value) {
                    "income" -> {
                        categoryRepository.getCategoriesByType(user.id, Constants.TRANSACTION_TYPE_INCOME)
                            .catch { e ->
                                e.printStackTrace()
                                _isLoading.value = false
                            }
                            .collect { categoryList ->
                                _categories.value = categoryList
                                _isLoading.value = false
                            }
                    }
                    "expense" -> {
                        categoryRepository.getCategoriesByType(user.id, Constants.TRANSACTION_TYPE_EXPENSE)
                            .catch { e ->
                                e.printStackTrace()
                                _isLoading.value = false
                            }
                            .collect { categoryList ->
                                _categories.value = categoryList
                                _isLoading.value = false
                            }
                    }
                    else -> {
                        categoryRepository.getAllCategories(user.id)
                            .catch { e ->
                                e.printStackTrace()
                                _isLoading.value = false
                            }
                            .collect { categoryList ->
                                _categories.value = categoryList
                                _isLoading.value = false
                            }
                    }
                }
            } else {
                _isLoading.value = false
            }
        }
    }

    fun setFilter(filter: String) {
        selectedFilter.value = filter
        loadCategories()
    }

    fun refresh() {
        loadCategories()
    }

    fun deleteCategory(categoryId: String) {
        viewModelScope.launch {
            try {
                categoryRepository.deleteCategory(categoryId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}








