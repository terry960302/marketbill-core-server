package kr.co.marketbill.marketbillcoreserver.domain.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class MessageReqDto(
    @JsonProperty("to")
    val to: String,
    @JsonProperty("message-type")
    val messageType: String,
    @JsonProperty("args")
    val args: List<*>,
)
