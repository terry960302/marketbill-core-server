package kr.co.marketbill.marketbillcoreserver.application.dto.request

import com.fasterxml.jackson.annotation.JsonProperty

data class MessageReqDto(
    @JsonProperty("to")
    val to: String,
    @JsonProperty("template")
    val template: String,
    @JsonProperty("args")
    val args: List<*>,
)
