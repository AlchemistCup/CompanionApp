package com.example.alchemistcompanion.network

import com.example.alchemistcompanion.ui.PlayerId
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query

interface MatchDataApiService {
    @GET("start")
    suspend fun startMatch(@Query("p1") player1: String, @Query("p2") player2: String): StartMatchResponse

    @GET("end-turn")
    suspend fun endTurn(@Query("n") n: Int): EndTurnResponse
}

@Serializable
data class StartMatchResponse(
    @SerialName(value = "match_id")
    val matchId: String? = null,
    val error: String? = null
)

@Serializable
data class EndTurnResponse(
    val score: Int,
    val blanks: Int
)