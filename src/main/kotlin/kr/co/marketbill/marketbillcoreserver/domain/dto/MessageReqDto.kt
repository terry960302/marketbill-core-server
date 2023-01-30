package kr.co.marketbill.marketbillcoreserver.domain.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class MessageReqDto(
    @JsonProperty("to")
    val to: String,
    @JsonProperty("template")
    val template: String,
    @JsonProperty("args")
    val args: List<*>,
)
