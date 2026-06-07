package com.info85.pdv85.ui.clients

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.info85.pdv85.data.remote.model.Client
import com.info85.pdv85.data.remote.model.ClientRequest
import com.info85.pdv85.data.repository.ClientRepository
import com.info85.pdv85.util.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ClientsViewModel : ViewModel() {
    private val repo = ClientRepository()

    private val _clients = MutableStateFlow<Result<List<Client>>>(Result.Loading)
    val clients: StateFlow<Result<List<Client>>> = _clients

    private val _actionResult = MutableStateFlow<Result<Unit>?>(null)
    val actionResult: StateFlow<Result<Unit>?> = _actionResult

    init { load() }

    fun load(q: String = "") {
        viewModelScope.launch {
            _clients.value = Result.Loading
            _clients.value = repo.getClients(q)
        }
    }

    fun save(request: ClientRequest) {
        viewModelScope.launch {
            _actionResult.value = Result.Loading
            val r = if (request.id == null) {
                when (val cr = repo.createClient(request)) {
                    is Result.Success -> Result.Success(Unit)
                    is Result.Error -> Result.Error(cr.message)
                    else -> Result.Error("Erro")
                }
            } else {
                repo.updateClient(request)
            }
            _actionResult.value = r
            if (r is Result.Success) load()
        }
    }

    fun delete(id: Int) {
        viewModelScope.launch {
            _actionResult.value = Result.Loading
            _actionResult.value = repo.deleteClient(id)
            if (_actionResult.value is Result.Success) load()
        }
    }

    fun clearActionResult() { _actionResult.value = null }
}
