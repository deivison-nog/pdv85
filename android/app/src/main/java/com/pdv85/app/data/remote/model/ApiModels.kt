package com.pdv85.app.data.remote.model

import com.google.gson.annotations.SerializedName

// ─────────────────────────────────────────────────────────────────────────────
// Auth
// ─────────────────────────────────────────────────────────────────────────────

data class LoginRequest(val username: String, val password: String)

data class AuthUser(val id: Int, val username: String, val role: String)

data class AuthResponse(val ok: Boolean, val user: AuthUser?, val error: String?)

// ─────────────────────────────────────────────────────────────────────────────
// Generic responses
// ─────────────────────────────────────────────────────────────────────────────

data class SimpleResponse(val ok: Boolean, val error: String?)

data class CreateResponse(val ok: Boolean, val id: Int?, val error: String?)

data class DeleteRequest(val id: Int)

// ─────────────────────────────────────────────────────────────────────────────
// Dashboard
// ─────────────────────────────────────────────────────────────────────────────

data class DashboardData(
    @SerializedName("sales_today") val salesToday: Double,
    @SerializedName("sales_month") val salesMonth: Double,
    @SerializedName("total_products") val totalProducts: Int,
    @SerializedName("low_stock_threshold") val lowStockThreshold: Int,
    @SerializedName("low_stock_count") val lowStockCount: Int
)

data class DashboardResponse(val ok: Boolean, val data: DashboardData?, val error: String?)

// ─────────────────────────────────────────────────────────────────────────────
// Products
// ─────────────────────────────────────────────────────────────────────────────

data class Product(
    val id: Int,
    val name: String,
    val upc: String?,
    @SerializedName("cost_price") val costPrice: String,
    val price: String,
    val stock: Int,
    @SerializedName("category_id") val categoryId: Int?,
    val category: String?
)

data class ProductListResponse(val ok: Boolean, val data: List<Product>?, val error: String?)

data class ProductRequest(
    val id: Int? = null,
    val name: String,
    val upc: String?,
    @SerializedName("category_id") val categoryId: Int?,
    @SerializedName("cost_price") val costPrice: String,
    val price: String,
    val stock: Int
)

// ─────────────────────────────────────────────────────────────────────────────
// Categories
// ─────────────────────────────────────────────────────────────────────────────

data class Category(val id: Int, val name: String)

data class CategoryListResponse(val ok: Boolean, val data: List<Category>?, val error: String?)

// ─────────────────────────────────────────────────────────────────────────────
// Sales
// ─────────────────────────────────────────────────────────────────────────────

data class Sale(
    val id: Int,
    val total: String,
    @SerializedName("discount_total") val discountTotal: String,
    @SerializedName("payment_method") val paymentMethod: String,
    @SerializedName("cash_paid") val cashPaid: String?,
    @SerializedName("cash_change") val cashChange: String?,
    val status: String,
    @SerializedName("created_at") val createdAt: String
)

data class SaleListResponse(val ok: Boolean, val data: List<Sale>?, val error: String?)

data class SaleItem(
    @SerializedName("product_id") val productId: Int,
    val qty: Int
)

data class CreateSaleRequest(
    @SerializedName("payment_method") val paymentMethod: String,
    @SerializedName("discount_total") val discountTotal: Double,
    val items: List<SaleItem>,
    @SerializedName("cash_paid") val cashPaid: Double? = null,
    @SerializedName("cash_change") val cashChange: Double? = null
)

data class CancelSaleRequest(val action: String = "cancel", val id: Int)

data class SaleResponse(
    val ok: Boolean,
    @SerializedName("sale_id") val saleId: Int?,
    val total: Double?,
    @SerializedName("cash_change") val cashChange: Double?,
    val error: String?
)

// ─────────────────────────────────────────────────────────────────────────────
// Reports
// ─────────────────────────────────────────────────────────────────────────────

data class ReportSeries(
    val date: String,
    @SerializedName("sales_total") val salesTotal: Double,
    @SerializedName("profit_net") val profitNet: Double
)

data class TopProduct(
    @SerializedName("product_id") val productId: Int,
    val name: String,
    val upc: String?,
    @SerializedName("profit_net") val profitNet: Double
)

data class ReportData(
    val from: String,
    val to: String,
    @SerializedName("total_sales") val totalSales: Double,
    @SerializedName("total_discount") val totalDiscount: Double,
    @SerializedName("profit_net") val profitNet: Double,
    val series: List<ReportSeries>,
    @SerializedName("top_products") val topProducts: List<TopProduct>
)

data class ReportResponse(val ok: Boolean, val data: ReportData?, val error: String?)

// ─────────────────────────────────────────────────────────────────────────────
// Clients
// ─────────────────────────────────────────────────────────────────────────────

data class Client(
    val id: Int,
    val name: String,
    val address: String?,
    val debt: String
)

data class ClientListResponse(val ok: Boolean, val data: List<Client>?, val error: String?)

data class ClientRequest(
    val id: Int? = null,
    val name: String,
    val address: String?,
    val debt: String
)

// ─────────────────────────────────────────────────────────────────────────────
// Suppliers
// ─────────────────────────────────────────────────────────────────────────────

data class Supplier(
    val id: Int,
    val name: String,
    val address: String?,
    @SerializedName("debt_to_supplier") val debtToSupplier: String
)

data class SupplierListResponse(val ok: Boolean, val data: List<Supplier>?, val error: String?)

data class SupplierRequest(
    val id: Int? = null,
    val name: String,
    val address: String?,
    @SerializedName("debt_to_supplier") val debtToSupplier: String
)

// ─────────────────────────────────────────────────────────────────────────────
// Settings
// ─────────────────────────────────────────────────────────────────────────────

data class SettingsData(
    @SerializedName("company_name") val companyName: String,
    @SerializedName("company_cnpj") val companyCnpj: String,
    @SerializedName("coupon_width_mm") val couponWidthMm: String,
    @SerializedName("coupon_copies") val couponCopies: String,
    @SerializedName("coupon_auto_print") val couponAutoPrint: String,
    val theme: String
)

data class SettingsResponse(val ok: Boolean, val data: SettingsData?, val error: String?)

data class SettingsRequest(
    @SerializedName("company_name") val companyName: String,
    @SerializedName("company_cnpj") val companyCnpj: String,
    @SerializedName("coupon_width_mm") val couponWidthMm: String,
    @SerializedName("coupon_copies") val couponCopies: String,
    @SerializedName("coupon_auto_print") val couponAutoPrint: String,
    val theme: String
)
