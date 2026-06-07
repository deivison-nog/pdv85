package com.pdv85.app.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdv85.app.data.remote.model.AuthUser
import com.pdv85.app.data.repository.AuthRepository
import com.pdv85.app.util.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val repo = AuthRepository()

    private val _state = MutableStateFlow<Result<AuthUser>?>(null)
    val state: StateFlow<Result<AuthUser>?> = _state

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _state.value = Result.Error("Preencha usuário e senha.")
            return
        }
        viewModelScope.launch {
            _state.value = Result.Loading
            _state.value = repo.login(username, password)
        }
    }
}
