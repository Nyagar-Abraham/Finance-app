package com.abraham.personalfinancemanagementapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abraham.personalfinancemanagementapp.data.model.Debt
import com.abraham.personalfinancemanagementapp.domain.repository.IAuthRepository
import com.abraham.personalfinancemanagementapp.domain.repository.IDebtRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel for Debt List screen
 */
class DebtListViewModel(
    private val authRepository: IAuthRepository,
    private val debtRepository: IDebtRepository
) : ViewModel() {

    private val _debts = MutableStateFlow<List<Debt>>(emptyList())
    val debts: StateFlow<List<Debt>> = _debts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val selectedFilter = MutableStateFlow("all") // "all", "owed", "lent", "unpaid"

    init {
        loadDebts()
    }

    private fun loadDebts() {
        viewModelScope.launch {
            _isLoading.value = true
            
            val user = authRepository.getCurrentUser()
            if (user != null) {
                when (selectedFilter.value) {
                    "owed" -> {
                        debtRepository.getDebtsByType(user.id, "owed")
                            .catch { e ->
                                e.printStackTrace()
                                _isLoading.value = false
                            }
                            .collect { debtList ->
                                _debts.value = debtList
                                _isLoading.value = false
                            }
                    }
                    "lent" -> {
                        debtRepository.getDebtsByType(user.id, "lent")
                            .catch { e ->
                                e.printStackTrace()
                                _isLoading.value = false
                            }
                            .collect { debtList ->
                                _debts.value = debtList
                                _isLoading.value = false
                            }
                    }
                    "unpaid" -> {
                        debtRepository.getUnpaidDebts(user.id)
                            .catch { e ->
                                e.printStackTrace()
                                _isLoading.value = false
                            }
                            .collect { debtList ->
                                _debts.value = debtList
                                _isLoading.value = false
                            }
                    }
                    else -> {
                        debtRepository.getAllDebts(user.id)
                            .catch { e ->
                                e.printStackTrace()
                                _isLoading.value = false
                            }
                            .collect { debtList ->
                                _debts.value = debtList
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
        loadDebts()
    }

    fun refresh() {
        loadDebts()
    }

    fun deleteDebt(debtId: String) {
        viewModelScope.launch {
            try {
                debtRepository.deleteDebt(debtId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}








