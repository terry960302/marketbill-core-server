package kr.co.marketbill.marketbillcoreserver.legacy.application.event

import org.springframework.context.ApplicationEvent

/**
 * 비즈니스 연결 신청 시 발행되는 이벤트
 */
class BizConnectionCreatedEvent(
    source: Any,
    val targetPhoneNo: String,
    val retailerName: String,
) : ApplicationEvent(source)
