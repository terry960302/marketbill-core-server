package kr.co.marketbill.marketbillcoreserver.application.dto.response

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class MessageResponseDto(
    @SerialName("requestId")
    val requestId: String,
    @SerialName("requestTime")
    val requestTime: String,
    @SerialName("statusCode")
    val statusCode: String,
    @SerialName("statusName")
    val statusName : String,
)
