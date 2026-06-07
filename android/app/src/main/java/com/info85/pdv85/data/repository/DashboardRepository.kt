package com.info85.pdv85.data.repository

import com.info85.pdv85.data.remote.NetworkClient
import com.info85.pdv85.data.remote.model.DashboardData
import com.info85.pdv85.util.Result

class DashboardRepository {
    private val api = NetworkClient.apiService

    suspend fun getDashboard(): Result<DashboardData> = try {
        val resp = api.getDashboard()
        val body = resp.body()
        if (resp.isSuccessful && body?.ok == true && body.data != null)
            Result.Success(body.data)
        else
            Result.Error(body?.error ?: "Erro ao carregar dashboard")
    } catch (e: Exception) {
        Result.Error("Erro de conexão: ${e.message}")
    }
}
