package com.abraham.personalfinancemanagementapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abraham.personalfinancemanagementapp.domain.repository.IAuthRepository
import com.abraham.personalfinancemanagementapp.domain.repository.ISavingsGoalRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel for Savings Goal List screen
 */
class SavingsGoalListViewModel(
    private val authRepository: IAuthRepository,
    private val savingsGoalRepository: ISavingsGoalRepository
) : ViewModel() {

    private val _savingsGoals = MutableStateFlow<List<com.abraham.personalfinancemanagementapp.data.model.SavingsGoal>>(emptyList())
    val savingsGoals: StateFlow<List<com.abraham.personalfinancemanagementapp.data.model.SavingsGoal>> = _savingsGoals.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val showIncompleteOnly = MutableStateFlow(false)

    init {
        loadSavingsGoals()
    }

    private fun loadSavingsGoals() {
        viewModelScope.launch {
            _isLoading.value = true
            
            val user = authRepository.getCurrentUser()
            if (user != null) {
                if (showIncompleteOnly.value) {
                    savingsGoalRepository.getIncompleteSavingsGoals(user.id)
                        .catch { e ->
                            e.printStackTrace()
                            _isLoading.value = false
                        }
                        .collect { savingsGoalList ->
                            _savingsGoals.value = savingsGoalList
                            _isLoading.value = false
                        }
                } else {
                    savingsGoalRepository.getAllSavingsGoals(user.id)
                        .catch { e ->
                            e.printStackTrace()
                            _isLoading.value = false
                        }
                        .collect { savingsGoalList ->
                            _savingsGoals.value = savingsGoalList
                            _isLoading.value = false
                        }
                }
            } else {
                _isLoading.value = false
            }
        }
    }

    fun toggleFilter() {
        showIncompleteOnly.value = !showIncompleteOnly.value
        loadSavingsGoals()
    }

    fun refresh() {
        loadSavingsGoals()
    }

    fun deleteSavingsGoal(savingsGoalId: String) {
        viewModelScope.launch {
            try {
                savingsGoalRepository.deleteSavingsGoal(savingsGoalId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}








