package com.pdv85.app.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdv85.app.data.remote.model.DashboardData
import com.pdv85.app.data.repository.DashboardRepository
import com.pdv85.app.util.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {
    private val repo = DashboardRepository()

    private val _state = MutableStateFlow<Result<DashboardData>>(Result.Loading)
    val state: StateFlow<Result<DashboardData>> = _state

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.value = Result.Loading
            _state.value = repo.getDashboard()
        }
    }
}
