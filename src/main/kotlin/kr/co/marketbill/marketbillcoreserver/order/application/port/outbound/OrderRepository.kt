package kr.co.marketbill.marketbillcoreserver.order.application.port.outbound

import java.time.LocalDate
import kr.co.marketbill.marketbillcoreserver.order.domain.model.CustomOrderItem
import kr.co.marketbill.marketbillcoreserver.order.domain.model.DailyOrderItem
import kr.co.marketbill.marketbillcoreserver.order.domain.model.OrderItem
import kr.co.marketbill.marketbillcoreserver.order.domain.model.OrderSheet
import kr.co.marketbill.marketbillcoreserver.order.domain.model.OrderSheetReceipt
import kr.co.marketbill.marketbillcoreserver.order.domain.vo.CustomOrderItemId
import kr.co.marketbill.marketbillcoreserver.order.domain.vo.DailyOrderItemId
import kr.co.marketbill.marketbillcoreserver.order.domain.vo.OrderItemId
import kr.co.marketbill.marketbillcoreserver.order.domain.vo.OrderSheetId
import kr.co.marketbill.marketbillcoreserver.shared.domain.vo.PageInfo
import kr.co.marketbill.marketbillcoreserver.shared.domain.vo.PageResult
import kr.co.marketbill.marketbillcoreserver.order.application.result.OrderSheetsAggregateResult
import kr.co.marketbill.marketbillcoreserver.user.domain.vo.UserId
import org.springframework.data.domain.Page

interface OrderRepository {
    // OrderSheet
    fun findOrderSheetById(id: OrderSheetId): OrderSheet?
    fun findOrderSheetsByDateRange(
        startDate: LocalDate?,
        endDate: LocalDate?,
        pageInfo: PageInfo
    ): PageResult<OrderSheet>

    fun saveOrderSheet(orderSheet: OrderSheet): OrderSheet
    fun deleteOrderSheet(id: OrderSheetId): Boolean

    // OrderItem
    fun findOrderItemsByDateRange(
        startDate: LocalDate?,
        endDate: LocalDate?,
        pageInfo: PageInfo
    ): PageResult<OrderItem>

    fun findOrderItemsByOrderSheetId(orderSheetId: OrderSheetId): List<OrderItem>
    fun saveOrderItem(orderItem: OrderItem): OrderItem
    fun updateOrderItemPrice(id: OrderItemId, price: Int): OrderItem

    // CustomOrderItem
    fun findCustomOrderItemsByOrderSheetId(orderSheetId: OrderSheetId): List<CustomOrderItem>
    fun saveCustomOrderItem(customOrderItem: CustomOrderItem): CustomOrderItem
    fun deleteCustomOrderItems(ids: List<CustomOrderItemId>): Boolean

    // DailyOrderItem
    fun findDailyOrderItems(startedAt: LocalDate?, endedAt: LocalDate?, pageInfo: PageInfo): PageResult<DailyOrderItem>

    fun updateDailyOrderItemPrice(id: DailyOrderItemId, price: Int): DailyOrderItem

    // OrderSheetReceipt
    fun findOrderSheetReceiptsByOrderSheetId(orderSheetId: OrderSheetId): List<OrderSheetReceipt>
    fun saveOrderSheetReceipt(receipt: OrderSheetReceipt): OrderSheetReceipt

    // Aggregate
    fun findOrderSheetsAggregateByDateRange(
        wholesalerId : UserId?,
        startDate: LocalDate?,
        endDate: LocalDate?,
        pageInfo: PageInfo
    ): PageResult<OrderSheetsAggregateResult>

    fun findOrderSheetsAggregateByDate(date: LocalDate?): OrderSheetsAggregateResult?
}
