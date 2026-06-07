package com.info85.pdv85.data.repository

import com.info85.pdv85.data.remote.NetworkClient
import com.info85.pdv85.data.remote.model.Category
import com.info85.pdv85.data.remote.model.DeleteRequest
import com.info85.pdv85.data.remote.model.Product
import com.info85.pdv85.data.remote.model.ProductRequest
import com.info85.pdv85.util.Result

class ProductRepository {
    private val api = NetworkClient.apiService

    suspend fun getProducts(q: String = "", limit: Int = 200): Result<List<Product>> = try {
        val resp = api.getProducts(q, limit)
        val body = resp.body()
        if (resp.isSuccessful && body?.ok == true)
            Result.Success(body.data ?: emptyList())
        else
            Result.Error(body?.error ?: "Erro ao buscar produtos")
    } catch (e: Exception) {
        Result.Error("Erro de conexão: ${e.message}")
    }

    suspend fun getCategories(): Result<List<Category>> = try {
        val resp = api.getCategories()
        val body = resp.body()
        if (resp.isSuccessful && body?.ok == true)
            Result.Success(body.data ?: emptyList())
        else
            Result.Error(body?.error ?: "Erro ao buscar categorias")
    } catch (e: Exception) {
        Result.Error("Erro de conexão: ${e.message}")
    }

    suspend fun createProduct(request: ProductRequest): Result<Int> = try {
        val resp = api.createProduct(request)
        val body = resp.body()
        if (resp.isSuccessful && body?.ok == true)
            Result.Success(body.id ?: 0)
        else
            Result.Error(body?.error ?: "Erro ao criar produto")
    } catch (e: Exception) {
        Result.Error("Erro de conexão: ${e.message}")
    }

    suspend fun updateProduct(request: ProductRequest): Result<Unit> = try {
        val resp = api.updateProduct(request)
        val body = resp.body()
        if (resp.isSuccessful && body?.ok == true)
            Result.Success(Unit)
        else
            Result.Error(body?.error ?: "Erro ao atualizar produto")
    } catch (e: Exception) {
        Result.Error("Erro de conexão: ${e.message}")
    }

    suspend fun deleteProduct(id: Int): Result<Unit> = try {
        val resp = api.deleteProduct(DeleteRequest(id))
        val body = resp.body()
        if (resp.isSuccessful && body?.ok == true)
            Result.Success(Unit)
        else
            Result.Error(body?.error ?: "Erro ao excluir produto")
    } catch (e: Exception) {
        Result.Error("Erro de conexão: ${e.message}")
    }
}
