package com.krai29.karanbalakrishnaraitask.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PortfolioResponseDto(
    @SerialName("data") val data: PortfolioDataDto
)

@Serializable
data class PortfolioDataDto(
    @SerialName("userHolding") val userHolding: List<HoldingDto>
)

@Serializable
data class HoldingDto(
    @SerialName("symbol") val symbol: String,
    @SerialName("quantity") val quantity: Int,
    @SerialName("ltp") val ltp: Double,
    @SerialName("avgPrice") val avgPrice: Double,
    @SerialName("close") val close: Double
)
