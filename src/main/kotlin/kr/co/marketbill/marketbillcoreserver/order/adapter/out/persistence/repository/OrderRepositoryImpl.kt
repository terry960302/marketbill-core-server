package kr.co.marketbill.marketbillcoreserver.order.adapter.out.persistence.repository

import java.time.LocalDate
import kr.co.marketbill.marketbillcoreserver.order.application.port.outbound.OrderRepository
import kr.co.marketbill.marketbillcoreserver.order.application.result.OrderSheetsAggregateResult
import kr.co.marketbill.marketbillcoreserver.order.domain.model.*
import kr.co.marketbill.marketbillcoreserver.order.domain.vo.*
import kr.co.marketbill.marketbillcoreserver.shared.domain.vo.PageInfo
import kr.co.marketbill.marketbillcoreserver.shared.domain.vo.PageResult
import kr.co.marketbill.marketbillcoreserver.shared.error.ErrorCode
import kr.co.marketbill.marketbillcoreserver.shared.error.MarketbillException
import kr.co.marketbill.marketbillcoreserver.user.domain.vo.UserId
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Repository

@Repository
class OrderRepositoryImpl(
    private val orderSheetCrudRepository: OrderSheetCrudRepository,
    private val orderItemCrudRepository: OrderItemCrudRepository,
    private val customOrderItemCrudRepository: CustomOrderItemCrudRepository,
    private val dailyOrderItemCrudRepository: DailyOrderItemCrudRepository,
    private val orderSheetReceiptCrudRepository: OrderSheetReceiptCrudRepository,
    private val orderSheetQueryRepository: OrderSheetQueryRepository
) : OrderRepository {

    // OrderSheet
    override fun findOrderSheetById(id: OrderSheetId): OrderSheet? {
        val jpo = orderSheetCrudRepository.findById(id.value).orElse(null) ?: return null
        return OrderSheet.fromJpo(jpo)
    }

    override fun findOrderSheetsByDateRange(
        startDate: LocalDate?,
        endDate: LocalDate?,
        pageInfo: PageInfo
    ): PageResult<OrderSheet> {
        val orderSheets =
            orderSheetQueryRepository.findOrderSheetsByDateRange(
                startDate,
                endDate,
                PageRequest.of(pageInfo.page, pageInfo.size)
            )

        return PageResult(
            content = orderSheets.content,
            pageInfo = pageInfo,
            totalElements = orderSheets.totalElements
        )
    }

    override fun saveOrderSheet(orderSheet: OrderSheet): OrderSheet {
        val jpo = OrderSheet.toJpo(orderSheet)
        val savedJpo = orderSheetCrudRepository.save(jpo)
        return OrderSheet.fromJpo(savedJpo)
    }

    override fun deleteOrderSheet(id: OrderSheetId): Boolean {
        if (!orderSheetCrudRepository.existsById(id.value)) {
            throw MarketbillException(ErrorCode.NO_ORDER_SHEET)
        }
        orderSheetCrudRepository.deleteById(id.value)
        return true
    }

    // OrderItem
    override fun findOrderItemsByDateRange(
        startDate: LocalDate?,
        endDate: LocalDate?,
        pageInfo: PageInfo
    ): PageResult<OrderItem> {
        // TODO: QueryDSL로 구현
        val jpos = orderItemCrudRepository.findAll()
        val domains = jpos.map { OrderItem.fromJpo(it) }

        return PageResult(
            content = domains,
            pageInfo = pageInfo,
            totalElements = domains.size.toLong()
        )
    }

    override fun findOrderItemsByOrderSheetId(orderSheetId: OrderSheetId): List<OrderItem> {
        val jpos = orderItemCrudRepository.findByOrderSheetId(orderSheetId.value)
        return jpos.map { OrderItem.fromJpo(it) }
    }

    override fun saveOrderItem(orderItem: OrderItem): OrderItem {
        val jpo = OrderItem.toJpo(orderItem)
        val savedJpo = orderItemCrudRepository.save(jpo)
        return OrderItem.fromJpo(savedJpo)
    }

    override fun updateOrderItemPrice(id: OrderItemId, price: Int): OrderItem {
        val jpo =
            orderItemCrudRepository.findById(id.value).orElseThrow {
                MarketbillException(ErrorCode.NO_ORDER_ITEM)
            }

        jpo.price = price
        val updatedJpo = orderItemCrudRepository.save(jpo)
        return OrderItem.fromJpo(updatedJpo)
    }

    // CustomOrderItem
    override fun findCustomOrderItemsByOrderSheetId(
        orderSheetId: OrderSheetId
    ): List<CustomOrderItem> {
        val jpos = customOrderItemCrudRepository.findByOrderSheetId(orderSheetId.value)
        return jpos.map { CustomOrderItem.fromJpo(it) }
    }

    override fun saveCustomOrderItem(customOrderItem: CustomOrderItem): CustomOrderItem {
        val jpo = CustomOrderItem.toJpo(customOrderItem)
        val savedJpo = customOrderItemCrudRepository.save(jpo)
        return CustomOrderItem.fromJpo(savedJpo)
    }

    override fun deleteCustomOrderItems(ids: List<CustomOrderItemId>): Boolean {
        val longIds = ids.map { it.value }
        customOrderItemCrudRepository.deleteByIdIn(longIds)
        return true
    }

    // DailyOrderItem
    override fun findDailyOrderItems(
        startedAt: LocalDate?,
        endedAt: LocalDate?,
        pageInfo: PageInfo
    ): PageResult<DailyOrderItem> {
        val dailyOrderItems = orderSheetQueryRepository.findDailyOrderItemsByDateRange(
            startedAt,
            endedAt,
            PageRequest.of(pageInfo.page, pageInfo.size)
        )

        return PageResult(
            content = dailyOrderItems.content,
            pageInfo = pageInfo,
            totalElements = dailyOrderItems.totalElements
        )
    }

    override fun updateDailyOrderItemPrice(id: DailyOrderItemId, price: Int): DailyOrderItem {
        val jpo =
            dailyOrderItemCrudRepository.findById(id.value).orElseThrow {
                MarketbillException(ErrorCode.NO_DAILY_ORDER_ITEM)
            }

        jpo.price = price
        val updatedJpo = dailyOrderItemCrudRepository.save(jpo)
        return DailyOrderItem.fromJpo(updatedJpo)
    }

    // OrderSheetReceipt
    override fun findOrderSheetReceiptsByOrderSheetId(
        orderSheetId: OrderSheetId
    ): List<OrderSheetReceipt> {
        val jpos = orderSheetReceiptCrudRepository.findByOrderSheetId(orderSheetId.value)
        return jpos.map { OrderSheetReceipt.fromJpo(it) }
    }

    override fun saveOrderSheetReceipt(receipt: OrderSheetReceipt): OrderSheetReceipt {
        val jpo = OrderSheetReceipt.toJpo(receipt)
        val savedJpo = orderSheetReceiptCrudRepository.save(jpo)
        return OrderSheetReceipt.fromJpo(savedJpo)
    }

    // Aggregate
    override fun findOrderSheetsAggregateByDateRange(
        wholesalerId: UserId?,
        startDate: LocalDate?,
        endDate: LocalDate?,
        pageInfo: PageInfo
    ): PageResult<OrderSheetsAggregateResult> {

        val items = orderSheetQueryRepository.findAllDailyOrderSheetsAggregates(
            wholesalerId,
            startDate,
            endDate,
            PageRequest.of(pageInfo.page, pageInfo.size)
        ).map { OrderSheetsAggregateResult.from(it.date, it.orderSheetsCount, it.flowerTypesCount) }
        return PageResult(
            content = items.content,
            pageInfo = pageInfo,
            totalElements = items.totalElements
        )

    }

    override fun findOrderSheetsAggregateByDate(date: LocalDate?): OrderSheetsAggregateResult? {
        // TODO: 실제 집계 로직 구현
        return null
    }
}
