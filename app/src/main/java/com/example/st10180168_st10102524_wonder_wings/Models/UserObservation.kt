package com.example.st10180168_st10102524_wonder_wings.Models

import android.location.Location

// Data class used to store user observations/sightings
data class UserObservation(
    val ObservationID: String,
    val UserID: String,
    val Date: String,
    val BirdName: String,
    val Amount: Int,
    val Location: Location,
    val Note: String,
    val PlaceName: String,
    var IsAtHotspot: Boolean
)
