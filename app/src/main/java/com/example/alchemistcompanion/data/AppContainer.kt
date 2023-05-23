package com.example.alchemistcompanion.data

import com.example.alchemistcompanion.network.MatchDataApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import retrofit2.Retrofit

interface AppContainer {
    val matchDataRepository: MatchDataRepository
}

class DefaultAppContainer : AppContainer {
    private val MATCH_DATA_URL = "http://matchdata.alchemist.live:9190"
    private val retrofit = Retrofit.Builder()
        .baseUrl(MATCH_DATA_URL)
        .addConverterFactory(Json.asConverterFactory(MediaType.get("application/json")))
        .build()

    private val retrofitService : MatchDataApiService by lazy {
        retrofit.create(MatchDataApiService::class.java)
    }

    override val matchDataRepository: MatchDataRepository by lazy {
        DefaultMatchDataRepository(retrofitService)
    }
}