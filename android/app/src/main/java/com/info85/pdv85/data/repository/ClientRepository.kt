package com.info85.pdv85.data.repository

import com.info85.pdv85.data.remote.NetworkClient
import com.info85.pdv85.data.remote.model.Client
import com.info85.pdv85.data.remote.model.ClientRequest
import com.info85.pdv85.data.remote.model.DeleteRequest
import com.info85.pdv85.util.Result

class ClientRepository {
    private val api = NetworkClient.apiService

    suspend fun getClients(q: String = ""): Result<List<Client>> = try {
        val resp = api.getClients(q)
        val body = resp.body()
        if (resp.isSuccessful && body?.ok == true)
            Result.Success(body.data ?: emptyList())
        else
            Result.Error(body?.error ?: "Erro ao buscar clientes")
    } catch (e: Exception) {
        Result.Error("Erro de conexão: ${e.message}")
    }

    suspend fun createClient(request: ClientRequest): Result<Int> = try {
        val resp = api.createClient(request)
        val body = resp.body()
        if (resp.isSuccessful && body?.ok == true)
            Result.Success(body.id ?: 0)
        else
            Result.Error(body?.error ?: "Erro ao criar cliente")
    } catch (e: Exception) {
        Result.Error("Erro de conexão: ${e.message}")
    }

    suspend fun updateClient(request: ClientRequest): Result<Unit> = try {
        val resp = api.updateClient(request)
        val body = resp.body()
        if (resp.isSuccessful && body?.ok == true)
            Result.Success(Unit)
        else
            Result.Error(body?.error ?: "Erro ao atualizar cliente")
    } catch (e: Exception) {
        Result.Error("Erro de conexão: ${e.message}")
    }

    suspend fun deleteClient(id: Int): Result<Unit> = try {
        val resp = api.deleteClient(DeleteRequest(id))
        val body = resp.body()
        if (resp.isSuccessful && body?.ok == true)
            Result.Success(Unit)
        else
            Result.Error(body?.error ?: "Erro ao excluir cliente")
    } catch (e: Exception) {
        Result.Error("Erro de conexão: ${e.message}")
    }
}
