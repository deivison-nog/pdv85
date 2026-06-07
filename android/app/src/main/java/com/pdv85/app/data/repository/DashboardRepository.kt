package com.pdv85.app.data.repository

import com.pdv85.app.data.remote.NetworkClient
import com.pdv85.app.data.remote.model.DashboardData
import com.pdv85.app.util.Result

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
