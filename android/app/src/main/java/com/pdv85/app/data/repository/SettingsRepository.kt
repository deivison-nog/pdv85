package com.pdv85.app.data.repository

import com.pdv85.app.data.remote.NetworkClient
import com.pdv85.app.data.remote.model.SettingsData
import com.pdv85.app.data.remote.model.SettingsRequest
import com.pdv85.app.util.Result

class SettingsRepository {
    private val api = NetworkClient.apiService

    suspend fun getSettings(): Result<SettingsData> = try {
        val resp = api.getSettings()
        val body = resp.body()
        if (resp.isSuccessful && body?.ok == true && body.data != null)
            Result.Success(body.data)
        else
            Result.Error(body?.error ?: "Erro ao carregar configurações")
    } catch (e: Exception) {
        Result.Error("Erro de conexão: ${e.message}")
    }

    suspend fun saveSettings(request: SettingsRequest): Result<Unit> = try {
        val resp = api.saveSettings(request)
        val body = resp.body()
        if (resp.isSuccessful && body?.ok == true)
            Result.Success(Unit)
        else
            Result.Error(body?.error ?: "Erro ao salvar configurações")
    } catch (e: Exception) {
        Result.Error("Erro de conexão: ${e.message}")
    }
}
