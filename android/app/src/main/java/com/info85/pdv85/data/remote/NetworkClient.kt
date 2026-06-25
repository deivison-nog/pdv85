package com.info85.pdv85.data.remote

import android.content.Context
import com.info85.pdv85.BuildConfig
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkClient {

    private lateinit var retrofit: Retrofit
    private val cookieStore = mutableMapOf<String, MutableList<Cookie>>()

    fun init(context: Context) {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                    else HttpLoggingInterceptor.Level.NONE
        }

        val cookieJar = object : CookieJar {
            override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                cookieStore.getOrPut(url.host) { mutableListOf() }.apply {
                    removeAll { existing -> cookies.any { it.name == existing.name } }
                    addAll(cookies)
                }
            }
            override fun loadForRequest(url: HttpUrl): List<Cookie> =
                cookieStore[url.host] ?: emptyList()
        }

        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val req = chain.request().newBuilder()
                    .addHeader("X-Requested-With", "XMLHttpRequest")
                    .addHeader("Accept", "application/json")
                    .build()
                chain.proceed(req)
            }
            .addInterceptor(logging)
            .cookieJar(cookieJar)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ApiService by lazy { retrofit.create(ApiService::class.java) }

    fun clearCookies() = cookieStore.clear()
}
