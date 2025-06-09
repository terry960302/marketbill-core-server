package kr.co.marketbill.marketbillcoreserver.application.event

import kr.co.marketbill.marketbillcoreserver.shared.constants.ApplyStatus
import org.springframework.context.ApplicationEvent

/**
 * 비즈니스 연결 상태 변경 시 발행되는 이벤트
 */
class BizConnectionUpdatedEvent(
    source: Any,
    val status: ApplyStatus,
    val targetPhoneNo: String,
    val wholesalerName: String,
) : ApplicationEvent(source)
