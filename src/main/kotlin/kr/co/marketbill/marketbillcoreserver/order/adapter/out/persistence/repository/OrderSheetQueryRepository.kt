package kr.co.marketbill.marketbillcoreserver.order.adapter.out.persistence.repository

import kr.co.marketbill.marketbillcoreserver.order.domain.model.DailyOrderItem
import kr.co.marketbill.marketbillcoreserver.order.domain.model.OrderSheet
import kr.co.marketbill.marketbillcoreserver.types.OrderSheetsAggregate
import kr.co.marketbill.marketbillcoreserver.user.domain.vo.UserId
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDate

interface OrderSheetQueryRepository {
    fun findOrderSheetsByDateRange(
        startDate: LocalDate?,
        endDate: LocalDate?,
        pageable: Pageable
    ): Page<OrderSheet>

    fun findDailyOrderItemsByDateRange(
        startDate: LocalDate?,
        endDate: LocalDate?,
        pageable: Pageable
    ): Page<DailyOrderItem>


    fun findAllDailyOrderSheetsAggregates(
        wholesalerId: UserId?,
        fromDate: LocalDate?,
        toDate: LocalDate?,
        pageable: Pageable
    ): Page<OrderSheetsAggregate>

    fun findDailyOrderSheetsAggregate(
        wholesalerId: UserId,
        date: LocalDate
    ): OrderSheetsAggregate?
}