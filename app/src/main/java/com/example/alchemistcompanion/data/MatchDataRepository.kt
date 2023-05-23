package com.example.alchemistcompanion.data

import com.example.alchemistcompanion.network.MatchDataApiService
import com.example.alchemistcompanion.network.StartMatchResponse

interface MatchDataRepository {
    suspend fun startMatch(player1: String, player2: String): StartMatchResponse
}

class DefaultMatchDataRepository(
    private val matchDataApiService: MatchDataApiService
) : MatchDataRepository {
    override suspend fun startMatch(player1: String, player2: String): StartMatchResponse {
        return matchDataApiService.startMatch(player1, player2)
    }
}