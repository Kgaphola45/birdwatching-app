package com.example.st10180168_st10102524_wonder_wings.Models

// Dataclass used when creating and tracking challenges
data class ChallengeObject(val description: String, var progress: Int, val required: Int, val pointsToGet: Int, val pointsAwarded: Int)
