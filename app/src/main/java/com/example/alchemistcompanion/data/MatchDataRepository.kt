package com.example.alchemistcompanion.data

import android.util.Log
import com.example.alchemistcompanion.network.ChallengeBody
import com.example.alchemistcompanion.network.ChallengeableWordsBody
import com.example.alchemistcompanion.network.EmptyBody
import com.example.alchemistcompanion.network.EndTurnBody
import com.example.alchemistcompanion.network.MatchDataApiService
import com.example.alchemistcompanion.network.ServerResponse
import com.example.alchemistcompanion.network.SetupMatchBody

interface MatchDataRepository {
    suspend fun setupMatch(player1: String, player2: String): ServerResponse<SetupMatchBody>
    suspend fun endTurn(matchId: String, turnNumber: Int, playerTime: Int): ServerResponse<EndTurnBody>
    suspend fun getChallengeableWords(matchId: String, turnNumber: Int): ServerResponse<ChallengeableWordsBody>
    suspend fun challenge(matchId: String, turnNumber: Int, words: List<String>): ServerResponse<ChallengeBody>
    suspend fun sendBlanks(matchId: String, turnNumber: Int, blankValues: List<String>): ServerResponse<EmptyBody>
}

class DefaultMatchDataRepository(
    private val matchDataApiService: MatchDataApiService
) : MatchDataRepository {
    private val TAG = "MatchDataRepository"
    override suspend fun setupMatch(player1: String, player2: String): ServerResponse<SetupMatchBody> {
        val res = matchDataApiService.setupMatch(player1, player2)
        Log.d(TAG, "$res")
        return res
    }

    override suspend fun endTurn(
        matchId: String,
        turnNumber: Int,
        playerTime: Int
    ): ServerResponse<EndTurnBody> {
        val res = matchDataApiService.endTurn(matchId, turnNumber, playerTime)
        Log.d(TAG, "$res")
        return res
    }

    override suspend fun getChallengeableWords(
        matchId: String,
        turnNumber: Int
    ): ServerResponse<ChallengeableWordsBody> {
        val res = matchDataApiService.getChallengeableWords(matchId, turnNumber)
        Log.d(TAG, "$res")
        return res
    }

    override suspend fun challenge(
        matchId: String,
        turnNumber: Int,
        words: List<String>
    ): ServerResponse<ChallengeBody> {
        val res = matchDataApiService.challenge(matchId, turnNumber, words)
        Log.d(TAG, "$res")
        return res
    }

    override suspend fun sendBlanks(
        matchId: String,
        turnNumber: Int,
        blankValues: List<String>
    ): ServerResponse<EmptyBody> {
        val res = matchDataApiService.sendBlanks(matchId, turnNumber, blankValues)
        Log.d(TAG, "$res")
        return res
    }
}