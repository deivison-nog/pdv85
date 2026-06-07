package com.info85.pdv85.data.remote

import com.info85.pdv85.data.remote.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ── Auth ──────────────────────────────────────────────────────────────────
    @GET("api/auth.php")
    suspend fun checkAuth(): Response<AuthResponse>

    @POST("api/auth.php")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @DELETE("api/auth.php")
    suspend fun logout(): Response<SimpleResponse>

    // ── Dashboard ─────────────────────────────────────────────────────────────
    @GET("api/dashboard.php")
    suspend fun getDashboard(): Response<DashboardResponse>

    // ── Products ──────────────────────────────────────────────────────────────
    @GET("api/products.php")
    suspend fun getProducts(
        @Query("q") q: String = "",
        @Query("limit") limit: Int = 0
    ): Response<ProductListResponse>

    @POST("api/products.php")
    suspend fun createProduct(@Body request: ProductRequest): Response<CreateResponse>

    @PUT("api/products.php")
    suspend fun updateProduct(@Body request: ProductRequest): Response<SimpleResponse>

    @DELETE("api/products.php")
    suspend fun deleteProduct(@Body request: DeleteRequest): Response<SimpleResponse>

    // ── Categories ────────────────────────────────────────────────────────────
    @GET("api/categories.php")
    suspend fun getCategories(): Response<CategoryListResponse>

    // ── Sales ─────────────────────────────────────────────────────────────────
    @GET("api/sales.php")
    suspend fun getSales(@Query("limit") limit: Int = 0): Response<SaleListResponse>

    @POST("api/sales.php")
    suspend fun createSale(@Body request: CreateSaleRequest): Response<SaleResponse>

    @POST("api/sales.php")
    suspend fun cancelSale(@Body request: CancelSaleRequest): Response<SimpleResponse>

    // ── Reports ───────────────────────────────────────────────────────────────
    @GET("api/reports.php")
    suspend fun getReport(
        @Query("from") from: String,
        @Query("to") to: String
    ): Response<ReportResponse>

    // ── Clients ───────────────────────────────────────────────────────────────
    @GET("api/clients.php")
    suspend fun getClients(@Query("q") q: String = ""): Response<ClientListResponse>

    @POST("api/clients.php")
    suspend fun createClient(@Body request: ClientRequest): Response<CreateResponse>

    @PUT("api/clients.php")
    suspend fun updateClient(@Body request: ClientRequest): Response<SimpleResponse>

    @DELETE("api/clients.php")
    suspend fun deleteClient(@Body request: DeleteRequest): Response<SimpleResponse>

    // ── Suppliers ─────────────────────────────────────────────────────────────
    @GET("api/suppliers.php")
    suspend fun getSuppliers(@Query("q") q: String = ""): Response<SupplierListResponse>

    @POST("api/suppliers.php")
    suspend fun createSupplier(@Body request: SupplierRequest): Response<CreateResponse>

    @PUT("api/suppliers.php")
    suspend fun updateSupplier(@Body request: SupplierRequest): Response<SimpleResponse>

    @DELETE("api/suppliers.php")
    suspend fun deleteSupplier(@Body request: DeleteRequest): Response<SimpleResponse>

    // ── Settings ──────────────────────────────────────────────────────────────
    @GET("api/settings.php")
    suspend fun getSettings(): Response<SettingsResponse>

    @POST("api/settings.php")
    suspend fun saveSettings(@Body request: SettingsRequest): Response<SimpleResponse>
}
