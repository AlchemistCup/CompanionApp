package com.example.alchemistcompanion.data

import com.example.alchemistcompanion.network.MatchDataApiService
import com.example.alchemistcompanion.network.SetupMatchResponse

interface MatchDataRepository {
    suspend fun setupMatch(player1: String, player2: String): SetupMatchResponse
}

class DefaultMatchDataRepository(
    private val matchDataApiService: MatchDataApiService
) : MatchDataRepository {
    private val TAG = "MatchDataRepository"
    override suspend fun setupMatch(player1: String, player2: String): SetupMatchResponse {
        return matchDataApiService.setupMatch(player1, player2)
        // val res = matchDataApiService.startMatch(player1, player2)
        // Log.d(tag, "$res")
        // return if (res.isSuccessful()) res.body() else res.errorBody()
        // return res.body()!!
    }
}