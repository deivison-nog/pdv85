package com.pdv85.app.data.repository

import com.pdv85.app.data.remote.NetworkClient
import com.pdv85.app.data.remote.model.ReportData
import com.pdv85.app.util.Result

class ReportRepository {
    private val api = NetworkClient.apiService

    suspend fun getReport(from: String, to: String): Result<ReportData> = try {
        val resp = api.getReport(from, to)
        val body = resp.body()
        if (resp.isSuccessful && body?.ok == true && body.data != null)
            Result.Success(body.data)
        else
            Result.Error(body?.error ?: "Erro ao gerar relatório")
    } catch (e: Exception) {
        Result.Error("Erro de conexão: ${e.message}")
    }
}
