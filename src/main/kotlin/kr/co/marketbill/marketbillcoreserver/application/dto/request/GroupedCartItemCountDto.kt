package kr.co.marketbill.marketbillcoreserver.application.dto.request

data class GroupedCartItemCountDto(
    val sessionId: Long,
    val count: Long,
)