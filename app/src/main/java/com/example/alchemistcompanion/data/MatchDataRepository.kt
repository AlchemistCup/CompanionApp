package com.example.alchemistcompanion.data

import android.util.Log
import com.example.alchemistcompanion.network.MatchDataApiService
import com.example.alchemistcompanion.network.StartMatchResponse

interface MatchDataRepository {
    suspend fun startMatch(player1: String, player2: String): StartMatchResponse
}

class DefaultMatchDataRepository(
    private val matchDataApiService: MatchDataApiService
) : MatchDataRepository {
    private val TAG = "MatchDataRepository"
    override suspend fun startMatch(player1: String, player2: String): StartMatchResponse {
        return matchDataApiService.startMatch(player1, player2)
        // val res = matchDataApiService.startMatch(player1, player2)
        // Log.d(tag, "$res")
        // return if (res.isSuccessful()) res.body() else res.errorBody()
        // return res.body()!!
    }
}