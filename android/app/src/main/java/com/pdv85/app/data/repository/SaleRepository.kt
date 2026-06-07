package com.pdv85.app.data.repository

import com.pdv85.app.data.remote.NetworkClient
import com.pdv85.app.data.remote.model.CancelSaleRequest
import com.pdv85.app.data.remote.model.CreateSaleRequest
import com.pdv85.app.data.remote.model.Sale
import com.pdv85.app.data.remote.model.SaleResponse
import com.pdv85.app.util.Result

class SaleRepository {
    private val api = NetworkClient.apiService

    suspend fun getSales(limit: Int = 150): Result<List<Sale>> = try {
        val resp = api.getSales(limit)
        val body = resp.body()
        if (resp.isSuccessful && body?.ok == true)
            Result.Success(body.data ?: emptyList())
        else
            Result.Error(body?.error ?: "Erro ao buscar vendas")
    } catch (e: Exception) {
        Result.Error("Erro de conexão: ${e.message}")
    }

    suspend fun createSale(request: CreateSaleRequest): Result<SaleResponse> = try {
        val resp = api.createSale(request)
        val body = resp.body()
        if (resp.isSuccessful && body?.ok == true && body != null)
            Result.Success(body)
        else
            Result.Error(body?.error ?: "Erro ao finalizar venda")
    } catch (e: Exception) {
        Result.Error("Erro de conexão: ${e.message}")
    }

    suspend fun cancelSale(id: Int): Result<Unit> = try {
        val resp = api.cancelSale(CancelSaleRequest(id = id))
        val body = resp.body()
        if (resp.isSuccessful && body?.ok == true)
            Result.Success(Unit)
        else
            Result.Error(body?.error ?: "Erro ao cancelar venda")
    } catch (e: Exception) {
        Result.Error("Erro de conexão: ${e.message}")
    }
}
