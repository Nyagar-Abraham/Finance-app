package com.abraham.personalfinancemanagementapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abraham.personalfinancemanagementapp.data.model.SavingsGoal
import com.abraham.personalfinancemanagementapp.domain.repository.IAuthRepository
import com.abraham.personalfinancemanagementapp.domain.repository.ISavingsGoalRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

/**
 * ViewModel for Add/Edit Savings Goal screen
 */
class AddSavingsGoalViewModel(
    private val authRepository: IAuthRepository,
    private val savingsGoalRepository: ISavingsGoalRepository,
    private val savingsGoalId: String? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow<AddSavingsGoalUiState>(AddSavingsGoalUiState.Idle)
    val uiState: StateFlow<AddSavingsGoalUiState> = _uiState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    var name = MutableStateFlow("")
    var targetAmount = MutableStateFlow("")
    var currentAmount = MutableStateFlow("0.0")
    var deadline = MutableStateFlow<Date?>(null)
    var icon = MutableStateFlow("💰")
    var color = MutableStateFlow("#4CAF50")

    init {
        if (savingsGoalId != null) {
            loadSavingsGoal()
        }
    }

    private fun loadSavingsGoal() {
        viewModelScope.launch {
            if (savingsGoalId != null) {
                _isLoading.value = true
                try {
                    val savingsGoal = savingsGoalRepository.getSavingsGoalById(savingsGoalId)
                    if (savingsGoal != null) {
                        name.value = savingsGoal.name
                        targetAmount.value = savingsGoal.targetAmount.toString()
                        currentAmount.value = savingsGoal.currentAmount.toString()
                        deadline.value = savingsGoal.deadline
                        icon.value = savingsGoal.icon
                        color.value = savingsGoal.color
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    _uiState.value = AddSavingsGoalUiState.Error(e.message ?: "Failed to load savings goal")
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }

    fun saveSavingsGoal() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = authRepository.getCurrentUser()
                if (user == null) {
                    _uiState.value = AddSavingsGoalUiState.Error("User not logged in")
                    return@launch
                }

                val targetAmountValue = targetAmount.value.toDoubleOrNull() ?: 0.0
                val currentAmountValue = currentAmount.value.toDoubleOrNull() ?: 0.0

                val savingsGoal = SavingsGoal(
                    id = savingsGoalId ?: "",
                    userId = user.id,
                    name = name.value.trim(),
                    targetAmount = targetAmountValue,
                    currentAmount = currentAmountValue,
                    deadline = deadline.value,
                    icon = icon.value,
                    color = color.value,
                    createdAt = Date()
                )

                if (savingsGoalId == null) {
                    savingsGoalRepository.addSavingsGoal(savingsGoal)
                } else {
                    savingsGoalRepository.updateSavingsGoal(savingsGoal)
                }

                _uiState.value = AddSavingsGoalUiState.Success
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = AddSavingsGoalUiState.Error(e.message ?: "Failed to save savings goal")
            } finally {
                _isLoading.value = false
            }
        }
    }
}

sealed class AddSavingsGoalUiState {
    data object Idle : AddSavingsGoalUiState()
    data object Success : AddSavingsGoalUiState()
    data class Error(val message: String) : AddSavingsGoalUiState()
}








