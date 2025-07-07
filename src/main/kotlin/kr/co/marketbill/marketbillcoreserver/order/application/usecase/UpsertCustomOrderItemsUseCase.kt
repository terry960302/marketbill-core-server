package kr.co.marketbill.marketbillcoreserver.order.application.usecase

import java.time.LocalDateTime
import kr.co.marketbill.marketbillcoreserver.order.application.command.UpsertCustomOrderItemsCommand
import kr.co.marketbill.marketbillcoreserver.order.application.port.outbound.OrderRepository
import kr.co.marketbill.marketbillcoreserver.order.application.result.CustomOrderItemResult
import kr.co.marketbill.marketbillcoreserver.order.domain.model.CustomOrderItem
import kr.co.marketbill.marketbillcoreserver.order.domain.vo.CustomOrderItemId
import kr.co.marketbill.marketbillcoreserver.order.domain.vo.FlowerGrade
import kr.co.marketbill.marketbillcoreserver.order.domain.vo.OrderSheetId
import kr.co.marketbill.marketbillcoreserver.order.domain.vo.Quantity
import kr.co.marketbill.marketbillcoreserver.shared.error.ErrorCode
import kr.co.marketbill.marketbillcoreserver.shared.error.MarketbillException
import org.springframework.stereotype.Component

@Component
class UpsertCustomOrderItemsUseCase(private val orderRepository: OrderRepository) {
    fun execute(command: UpsertCustomOrderItemsCommand): List<CustomOrderItemResult> {
        val orderSheetId = OrderSheetId.from(command.orderSheetId)

        // 주문서가 존재하는지 확인
        val orderSheet =
            orderRepository.findOrderSheetById(orderSheetId)
                ?: throw MarketbillException(ErrorCode.NO_ORDER_SHEET)

        val now = LocalDateTime.now()
        val customOrderItems =
            command.items.map { input ->
                CustomOrderItem(
                    id = input.id?.let { CustomOrderItemId.from(it) },
                    orderSheet = orderSheet,
                    retailer = orderSheet.retailer,
                    wholesaler = orderSheet.wholesaler,
                    flowerName = input.flowerName,
                    flowerTypeName = input.flowerTypeName,
                    quantity = input.quantity?.let { Quantity.from(it) },
                    grade = input.grade?.let { FlowerGrade.valueOf(it.name) },
                    price = input.price,
                    createdAt = now,
                    updatedAt = now,
                    deletedAt = null
                )
            }

        val savedCustomOrderItems = customOrderItems.map { orderRepository.saveCustomOrderItem(it) }

        return savedCustomOrderItems.map { CustomOrderItemResult.from(it) }
    }
}
