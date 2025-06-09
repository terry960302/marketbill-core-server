package kr.co.marketbill.marketbillcoreserver.application.event

import kotlinx.coroutines.runBlocking
import kr.co.marketbill.marketbillcoreserver.application.service.common.MessagingService
import kr.co.marketbill.marketbillcoreserver.shared.constants.ApplyStatus
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class BizConnectionEventHandler(
    private val messagingService: MessagingService,
) {
    @EventListener
    fun handleCreatedEvent(event: BizConnectionCreatedEvent) = runBlocking {
        messagingService.sendApplyBizConnectionSMS(event.targetPhoneNo, event.retailerName)
    }

    @EventListener
    fun handleUpdatedEvent(event: BizConnectionUpdatedEvent) = runBlocking {
        when (event.status) {
            ApplyStatus.CONFIRMED -> messagingService.sendConfirmBizConnectionSMS(
                to = event.targetPhoneNo,
                wholesalerName = event.wholesalerName,
            )
            ApplyStatus.REJECTED -> messagingService.sendRejectBizConnectionSMS(
                to = event.targetPhoneNo,
                wholesalerName = event.wholesalerName,
            )
            else -> {
                // nothing
            }
        }
    }
}
