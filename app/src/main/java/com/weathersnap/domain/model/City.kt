package com.weathersnap.domain.model

data class City(
    val id: Int,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String,
    val admin1: String?
) {
    val displayName: String
        get() = "$name, $country"
}
