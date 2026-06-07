package com.info85.pdv85.data.repository

import com.info85.pdv85.data.remote.NetworkClient
import com.info85.pdv85.data.remote.model.AuthUser
import com.info85.pdv85.data.remote.model.LoginRequest
import com.info85.pdv85.util.Result

class AuthRepository {
    private val api = NetworkClient.apiService

    suspend fun login(username: String, password: String): Result<AuthUser> = try {
        val resp = api.login(LoginRequest(username, password))
        val body = resp.body()
        if (resp.isSuccessful && body?.ok == true && body.user != null)
            Result.Success(body.user)
        else
            Result.Error(body?.error ?: "Erro ao fazer login")
    } catch (e: Exception) {
        Result.Error("Erro de conexão: ${e.message}")
    }

    suspend fun logout(): Result<Unit> {
        return try {
            api.logout()
            NetworkClient.clearCookies()
            Result.Success(Unit)
        } catch (_: Exception) {
            NetworkClient.clearCookies()
            Result.Success(Unit)
        }
    }

    suspend fun checkAuth(): Result<AuthUser> = try {
        val resp = api.checkAuth()
        val body = resp.body()
        if (resp.isSuccessful && body?.ok == true && body.user != null)
            Result.Success(body.user)
        else
            Result.Error("Não autenticado")
    } catch (e: Exception) {
        Result.Error("Erro de conexão: ${e.message}")
    }
}
