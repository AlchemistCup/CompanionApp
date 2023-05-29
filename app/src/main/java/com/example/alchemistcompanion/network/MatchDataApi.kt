package com.example.alchemistcompanion.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface MatchDataApiService {
    @GET("setup")
    suspend fun setupMatch(
        @Query("p1") player1: String,
        @Query("p2") player2: String
    ): ServerResponse<SetupMatchBody>

    @GET("end-turn")
    suspend fun endTurn(
        @Query("match_id") matchId: String,
        @Query("turn_number") turnNumber: Int,
        @Query("player_time") playerTime: Int
    ): ServerResponse<EndTurnBody>

    @GET("challengeable-words")
    suspend fun getChallengeableWords(
        @Query("match_id") matchId: String,
        @Query("turn_number") turnNumber: Int
    ): ServerResponse<ChallengeableWordsBody>

    @GET("challenge")
    suspend fun challenge(
        @Query("match_id") matchId: String,
        @Query("turn_number") turnNumber: Int,
        @Query("words") words: List<String>
    ): ServerResponse<ChallengeBody>

    @POST("blanks")
    suspend fun sendBlanks(
        @Query("match_id") matchId: String,
        @Query("turn_number") turnNumber: Int,
        @Body blankValues: List<String>
    ): ServerResponse<EmptyBody>
}

@Serializable
data class ServerResponse<T>(
    val body: T? = null,
    @SerialName(value = "error")
    private val _error: String? = null
) {
    val success: Boolean
        get() = body != null

    val error: String?
        get() = if (!success && _error == null) "Unknown error: response body was null but no error reason was provided" else _error
}

@Serializable
data class SetupMatchBody(
    @SerialName(value = "match_id")
    val matchId: String
)

@Serializable
data class EndTurnBody(
    val score: Int,
    val blanks: Int
)

@Serializable
data class ChallengeableWordsBody(
    val words: List<String>
)

@Serializable
data class ChallengeBody(
    val successful: Boolean,
    @SerialName(value = "challenger_penalty")
    val challengerPenalty: Int,
    @SerialName(value = "undone_move_score")
    val undoneMoveScore: Int
)

@Serializable
data class EmptyBody(
    val success: Boolean = true
)