package com.abraham.personalfinancemanagementapp.data.service

import com.abraham.personalfinancemanagementapp.util.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Service interface for currency conversion API
 */
interface CurrencyApiService {
    @GET("latest/{base}")
    suspend fun getLatestRates(@Path("base") baseCurrency: String): ExchangeRateResponse
}

/**
 * Response model for exchange rates
 */
data class ExchangeRateResponse(
    val base: String,
    val date: String,
    val rates: Map<String, Double>
)

/**
 * Currency converter service
 */
class CurrencyConverterService {
    
    private val apiService: CurrencyApiService by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.CURRENCY_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CurrencyApiService::class.java)
    }
    
    /**
     * Convert amount from one currency to another
     */
    suspend fun convert(
        amount: Double,
        fromCurrency: String,
        toCurrency: String
    ): Result<Double> = withContext(Dispatchers.IO) {
        try {
            if (fromCurrency == toCurrency) {
                return@withContext Result.success(amount)
            }
            
            val response = apiService.getLatestRates(fromCurrency)
            val rate = response.rates[toCurrency]
            
            if (rate != null) {
                Result.success(amount * rate)
            } else {
                Result.failure(Exception("Exchange rate not found for $toCurrency"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get all available exchange rates for a base currency
     */
    suspend fun getExchangeRates(baseCurrency: String): Result<Map<String, Double>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getLatestRates(baseCurrency)
            Result.success(response.rates)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get list of supported currencies
     */
    suspend fun getSupportedCurrencies(): Result<List<String>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getLatestRates("USD")
            Result.success(response.rates.keys.toList().sorted())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}








