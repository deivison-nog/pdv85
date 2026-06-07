package com.pdv85.app.data.repository

import com.pdv85.app.data.remote.NetworkClient
import com.pdv85.app.data.remote.model.DeleteRequest
import com.pdv85.app.data.remote.model.Supplier
import com.pdv85.app.data.remote.model.SupplierRequest
import com.pdv85.app.util.Result

class SupplierRepository {
    private val api = NetworkClient.apiService

    suspend fun getSuppliers(q: String = ""): Result<List<Supplier>> = try {
        val resp = api.getSuppliers(q)
        val body = resp.body()
        if (resp.isSuccessful && body?.ok == true)
            Result.Success(body.data ?: emptyList())
        else
            Result.Error(body?.error ?: "Erro ao buscar fornecedores")
    } catch (e: Exception) {
        Result.Error("Erro de conexão: ${e.message}")
    }

    suspend fun createSupplier(request: SupplierRequest): Result<Int> = try {
        val resp = api.createSupplier(request)
        val body = resp.body()
        if (resp.isSuccessful && body?.ok == true)
            Result.Success(body.id ?: 0)
        else
            Result.Error(body?.error ?: "Erro ao criar fornecedor")
    } catch (e: Exception) {
        Result.Error("Erro de conexão: ${e.message}")
    }

    suspend fun updateSupplier(request: SupplierRequest): Result<Unit> = try {
        val resp = api.updateSupplier(request)
        val body = resp.body()
        if (resp.isSuccessful && body?.ok == true)
            Result.Success(Unit)
        else
            Result.Error(body?.error ?: "Erro ao atualizar fornecedor")
    } catch (e: Exception) {
        Result.Error("Erro de conexão: ${e.message}")
    }

    suspend fun deleteSupplier(id: Int): Result<Unit> = try {
        val resp = api.deleteSupplier(DeleteRequest(id))
        val body = resp.body()
        if (resp.isSuccessful && body?.ok == true)
            Result.Success(Unit)
        else
            Result.Error(body?.error ?: "Erro ao excluir fornecedor")
    } catch (e: Exception) {
        Result.Error("Erro de conexão: ${e.message}")
    }
}
