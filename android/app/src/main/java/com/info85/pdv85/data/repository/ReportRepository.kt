package com.info85.pdv85.data.repository

import com.info85.pdv85.data.remote.NetworkClient
import com.info85.pdv85.data.remote.model.ReportData
import com.info85.pdv85.util.Result

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
