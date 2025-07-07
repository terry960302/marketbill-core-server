package kr.co.marketbill.marketbillcoreserver.order.application.usecase

import java.time.LocalDateTime
import kr.co.marketbill.marketbillcoreserver.order.application.command.OrderCartItemsCommand
import kr.co.marketbill.marketbillcoreserver.order.application.port.outbound.OrderRepository
import kr.co.marketbill.marketbillcoreserver.order.application.result.OrderSheetResult
import kr.co.marketbill.marketbillcoreserver.order.domain.model.OrderSheet
import org.springframework.stereotype.Component

@Component
class OrderCartItemsUseCase(private val orderRepository: OrderRepository) {
    fun execute(command: OrderCartItemsCommand): OrderSheetResult? {
        // TODO: 장바구니에서 주문서로 변환하는 로직 구현
        // 현재는 기본 구조만 제공

        val now = LocalDateTime.now()
        val orderSheet =
                OrderSheet(
                        id = null,
                        orderNo = generateOrderNo(),
                        retailer = null, // TODO: 현재 사용자 정보에서 가져오기
                        wholesaler = null, // TODO: 장바구니에서 가져오기
                        orderItems = emptyList(), // TODO: 장바구니 항목들을 주문 항목으로 변환
                        customOrderItems = emptyList(),
                        totalFlowerQuantity = 0,
                        totalFlowerTypeCount = 0,
                        totalFlowerPrice = 0,
                        hasReceipt = false,
                        orderSheetReceipts = emptyList(),
                        recentReceipt = null,
                        isPriceUpdated = false,
                        memo = null,
                        createdAt = now,
                        updatedAt = now,
                        deletedAt = null
                )

        val savedOrderSheet = orderRepository.saveOrderSheet(orderSheet)
        return OrderSheetResult.from(savedOrderSheet)
    }

    private fun generateOrderNo(): String {
        return "ORDER-${System.currentTimeMillis()}"
    }
}
