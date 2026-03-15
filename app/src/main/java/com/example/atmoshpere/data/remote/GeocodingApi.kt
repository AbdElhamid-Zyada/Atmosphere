package com.example.atmoshpere.data.remote

import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class GeocodingResponse(
    @SerializedName("display_name") val displayName: String,
    @SerializedName("lat") val lat: String,
    @SerializedName("lon") val lon: String,
    @SerializedName("address") val address: Address?
)

data class Address(
    @SerializedName("country") val country: String?,
    @SerializedName("city") val city: String?,
    @SerializedName("town") val town: String?,
    @SerializedName("village") val village: String?
)

interface GeocodingApi {
    @GET("search")
    suspend fun autocomplete(
        @Query("q") query: String,
        @Query("format") format: String = "json",
        @Query("addressdetails") address: Int = 1,
        @Query("limit") limit: Int = 5
    ): Response<List<GeocodingResponse>>
}

object GeocodingClient {
    private const val BASE_URL = "https://nominatim.openstreetmap.org/"

    private val client = OkHttpClient.Builder().addInterceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("User-Agent", "Atmoshpere/1.0")
            .build()
        chain.proceed(request)
    }.build()

    val geocodingApi: GeocodingApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeocodingApi::class.java)
    }
}
