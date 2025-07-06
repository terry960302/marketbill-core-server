package kr.co.marketbill.marketbillcoreserver.order.adapter.out.persistence.repository

import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.impl.JPAQueryFactory
import kr.co.marketbill.marketbillcoreserver.flower.adapter.out.persistence.entity.QFlowerJpo
import kr.co.marketbill.marketbillcoreserver.order.adapter.out.persistence.entity.DailyOrderItemJpo
import java.time.LocalDate
import kr.co.marketbill.marketbillcoreserver.order.adapter.out.persistence.entity.OrderSheetJpo
import kr.co.marketbill.marketbillcoreserver.order.adapter.out.persistence.entity.QDailyOrderItemJpo
import kr.co.marketbill.marketbillcoreserver.order.adapter.out.persistence.entity.QOrderItemJpo
import kr.co.marketbill.marketbillcoreserver.order.adapter.out.persistence.entity.QOrderSheetJpo
import kr.co.marketbill.marketbillcoreserver.order.adapter.out.persistence.entity.QOrderSheetReceiptJpo
import kr.co.marketbill.marketbillcoreserver.order.domain.model.DailyOrderItem
import kr.co.marketbill.marketbillcoreserver.order.domain.model.OrderSheet
import kr.co.marketbill.marketbillcoreserver.shared.adapter.out.persistence.mapper.toPageJpoResponse
import kr.co.marketbill.marketbillcoreserver.types.OrderSheetsAggregate
import kr.co.marketbill.marketbillcoreserver.user.domain.vo.UserId
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class OrderSheetQueryRepositoryImpl(private val queryFactory: JPAQueryFactory) : OrderSheetQueryRepository {

    private val orderSheetJpo = QOrderSheetJpo.orderSheetJpo
    private val dailyOrderItemJpo = QDailyOrderItemJpo.dailyOrderItemJpo
    private val orderItemJpo = QOrderItemJpo.orderItemJpo
    private val flowerJpo = QFlowerJpo.flowerJpo
    private val orderSheetReceiptJpo = QOrderSheetReceiptJpo.orderSheetReceiptJpo


    override fun findOrderSheetsByDateRange(
        startDate: LocalDate?,
        endDate: LocalDate?,
        pageable: Pageable
    ): Page<OrderSheet> {
        val query =
            queryFactory
                .selectFrom(orderSheetJpo)
                .where(
                    orderSheetCreatedAtGoe(startDate),
                    orderSheetCreatedAtLoe(endDate)
                )
                .orderBy(orderSheetJpo.createdAt.desc())

        return query.toPageJpoResponse<OrderSheetJpo>(pageable).map { OrderSheet.fromJpo(it) }
    }

    override fun findDailyOrderItemsByDateRange(
        startDate: LocalDate?,
        endDate: LocalDate?,
        pageable: Pageable
    ): Page<DailyOrderItem> {
        val query =
            queryFactory
                .selectFrom(dailyOrderItemJpo)
                .where(
                    dailyOrderItemCreatedAtGoe(startDate),
                    dailyOrderItemCreatedAtLoe(endDate)
                )
                .orderBy(orderSheetJpo.createdAt.desc())

        return query.toPageJpoResponse<DailyOrderItemJpo>(pageable).map { DailyOrderItem.fromJpo(it) }
    }


    override fun findAllDailyOrderSheetsAggregates(
        wholesalerId: UserId?,
        fromDate: LocalDate?,
        toDate: LocalDate?,
        pageable: Pageable
    ): Page<OrderSheetsAggregate> {
        val query = queryFactory
            .select(
                Projections.constructor(
                    OrderSheetsAggregate::class.java,
                    truncatedDate.`as`("date"),
                    flowerJpo.flowerTypeJpo.id.countDistinct().`as`("flowerTypesCount"),
                    orderSheetJpo.id.countDistinct().`as`("orderSheetsCount")
                )
            )
            .from(orderSheetJpo)
            .join(orderItemJpo).on(orderItemJpo.orderSheet.id.eq(orderItemJpo.id))
            .join(flowerJpo).on(flowerJpo.id.eq(orderItemJpo.flower.id))
            .join(orderSheetReceiptJpo).on(orderSheetReceiptJpo.orderSheet.id.eq(orderSheetJpo.id))
            .where(
                orderSheetJpo.wholesaler.id.eq(wholesalerId?.value),
                createdAtBetween(fromDate, toDate)
            )
            .groupBy(truncatedDate)
            .orderBy(truncatedDate.asc())

        return query.toPageJpoResponse(pageable)
    }

    override fun findDailyOrderSheetsAggregate(
        wholesalerId: UserId,
        date: LocalDate
    ): OrderSheetsAggregate? {
        return queryFactory
            .select(
                Projections.constructor(
                    OrderSheetsAggregate::class.java,
                    truncatedDate.`as`("date"),
                    orderSheetJpo.id.countDistinct().`as`("orderSheetsCount"),
                    flowerJpo.flowerTypeJpo.id.countDistinct().`as`("flowerTypesCount")
                )
            )
            .from(orderSheetJpo)
            .join(orderItemJpo).on(orderItemJpo.orderSheet.id.eq(orderSheetJpo.id))
            .join(flowerJpo).on(flowerJpo.id.eq(orderItemJpo.flower.id))
            .where(
                orderSheetJpo.wholesaler.id.eq(wholesalerId.value),
                truncatedDate.eq(date)
            )
            .fetchOne()
    }

    private val truncatedDate = Expressions.dateTemplate(
        LocalDate::class.java,
        "function('date_trunc', 'day', {0})",
        orderSheetJpo.createdAt
    )

    private fun createdAtBetween(fromDate: LocalDate?, toDate: LocalDate?): BooleanExpression? {
        if (fromDate == null || toDate == null) return null
        return orderSheetJpo.createdAt.between(fromDate.atStartOfDay(), toDate.atTime(23, 59, 59))
    }

    private fun orderSheetCreatedAtGoe(startDate: LocalDate?): BooleanExpression? =
        startDate?.let { orderSheetJpo.createdAt.goe(it.atStartOfDay()) }

    private fun orderSheetCreatedAtLoe(endDate: LocalDate?): BooleanExpression? =
        endDate?.let { orderSheetJpo.createdAt.loe(it.atTime(23, 59, 59)) }

    private fun dailyOrderItemCreatedAtGoe(startDate: LocalDate?): BooleanExpression? =
        startDate?.let { dailyOrderItemJpo.createdAt.goe(it.atStartOfDay()) }

    private fun dailyOrderItemCreatedAtLoe(endDate: LocalDate?): BooleanExpression? =
        endDate?.let { dailyOrderItemJpo.createdAt.loe(it.atTime(23, 59, 59)) }

}