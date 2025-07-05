package kr.co.marketbill.marketbillcoreserver.legacy.application.dto.request

data class GroupedCartItemCountDto(
    val sessionId: Long,
    val count: Long,
)