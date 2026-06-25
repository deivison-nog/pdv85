package com.info85.pdv85.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.info85.pdv85.data.remote.model.SettingsData
import com.info85.pdv85.data.remote.model.SettingsRequest
import com.info85.pdv85.data.repository.SettingsRepository
import com.info85.pdv85.util.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {
    private val repo = SettingsRepository()

    private val _settings = MutableStateFlow<Result<SettingsData>>(Result.Loading)
    val settings: StateFlow<Result<SettingsData>> = _settings

    private val _saveResult = MutableStateFlow<Result<Unit>?>(null)
    val saveResult: StateFlow<Result<Unit>?> = _saveResult

    init { load() }

    fun load() {
        viewModelScope.launch {
            _settings.value = Result.Loading
            _settings.value = repo.getSettings()
        }
    }

    fun save(request: SettingsRequest) {
        viewModelScope.launch {
            _saveResult.value = Result.Loading
            _saveResult.value = repo.saveSettings(request)
        }
    }

    fun clearSaveResult() { _saveResult.value = null }
}
