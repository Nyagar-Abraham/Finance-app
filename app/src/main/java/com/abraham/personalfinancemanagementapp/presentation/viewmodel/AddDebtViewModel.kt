package com.abraham.personalfinancemanagementapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abraham.personalfinancemanagementapp.data.model.Debt
import com.abraham.personalfinancemanagementapp.domain.repository.IAuthRepository
import com.abraham.personalfinancemanagementapp.domain.repository.IDebtRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

/**
 * ViewModel for Add/Edit Debt screen
 */
class AddDebtViewModel(
    private val authRepository: IAuthRepository,
    private val debtRepository: IDebtRepository,
    private val debtId: String? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow<AddDebtUiState>(AddDebtUiState.Idle)
    val uiState: StateFlow<AddDebtUiState> = _uiState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    var selectedType = MutableStateFlow("owed") // "owed" or "lent"
    var creditorOrDebtor = MutableStateFlow("")
    var amount = MutableStateFlow("")
    var remainingAmount = MutableStateFlow("")
    var dueDate = MutableStateFlow<Date?>(null)
    var notes = MutableStateFlow("")
    var isPaid = MutableStateFlow(false)

    init {
        if (debtId != null) {
            loadDebt()
        }
    }

    private fun loadDebt() {
        viewModelScope.launch {
            if (debtId != null) {
                _isLoading.value = true
                try {
                    val debt = debtRepository.getDebtById(debtId)
                    if (debt != null) {
                        selectedType.value = debt.type
                        creditorOrDebtor.value = debt.creditorOrDebtor
                        amount.value = debt.amount.toString()
                        remainingAmount.value = debt.remainingAmount.toString()
                        dueDate.value = debt.dueDate
                        notes.value = debt.notes
                        isPaid.value = debt.isPaid
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    _uiState.value = AddDebtUiState.Error(e.message ?: "Failed to load debt")
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }

    fun saveDebt() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = authRepository.getCurrentUser()
                if (user == null) {
                    _uiState.value = AddDebtUiState.Error("User not logged in")
                    return@launch
                }

                val amountValue = amount.value.toDoubleOrNull() ?: 0.0
                val remainingAmountValue = remainingAmount.value.toDoubleOrNull() ?: amountValue

                val debt = Debt(
                    id = debtId ?: "",
                    userId = user.id,
                    type = selectedType.value,
                    creditorOrDebtor = creditorOrDebtor.value.trim(),
                    amount = amountValue,
                    remainingAmount = remainingAmountValue,
                    dueDate = dueDate.value,
                    notes = notes.value.trim(),
                    isPaid = isPaid.value,
                    createdAt = Date()
                )

                if (debtId == null) {
                    debtRepository.addDebt(debt)
                } else {
                    debtRepository.updateDebt(debt)
                }

                _uiState.value = AddDebtUiState.Success
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = AddDebtUiState.Error(e.message ?: "Failed to save debt")
            } finally {
                _isLoading.value = false
            }
        }
    }
}

sealed class AddDebtUiState {
    data object Idle : AddDebtUiState()
    data object Success : AddDebtUiState()
    data class Error(val message: String) : AddDebtUiState()
}








