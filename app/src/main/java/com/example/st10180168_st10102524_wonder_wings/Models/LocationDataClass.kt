package com.example.st10180168_st10102524_wonder_wings.Models

// A temp dataclass only used to extract data from the response from e-bird when getting hotspots near the user
data class LocationDataClass(
    val id: String,
    val countryCode: String,
    val regionCode: String,
    val unused1: String,
    val latitude: Double,
    val longitude: Double,
    val name: String,
    val date: String,
    val unused2: Int
)