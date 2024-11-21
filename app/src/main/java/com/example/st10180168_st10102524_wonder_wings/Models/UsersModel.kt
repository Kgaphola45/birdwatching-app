package com.example.st10180168_st10102524_wonder_wings.Models

// Data class used to store a user
data class UsersModel(
    var UserID: String = "",
    var Name: String = "",
    var Surname: String = "",
    var isUnitKM: Boolean = true,
    var MaxDistance: Double = 5.0,
    var ChallengePoints: Int = 0,
    var mapStyleIsDark: Boolean = false
)
