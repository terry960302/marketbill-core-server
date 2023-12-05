package kr.co.marketbill.marketbillcoreserver.domain.repository.order

import com.querydsl.core.types.Ops
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.impl.JPAQueryFactory
import kr.co.marketbill.marketbillcoreserver.constants.AccountRole
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.OrderSheet
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.QOrderSheet
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import java.time.LocalDate


class OrderSheetRepositoryImpl : QuerydslRepositorySupport(OrderSheet::class.java), OrderSheetRepositoryCustom {
    @Autowired
    private lateinit var queryFactory: JPAQueryFactory

    override fun findAllWithFilters(
        pageable: Pageable,
        userId: Long?,
        role: AccountRole?,
        date: LocalDate?
    ): Page<OrderSheet> {
        val orderSheet = QOrderSheet.orderSheet

        val query = queryFactory.selectFrom(orderSheet).where(eqUserId(userId, role), eqDate(date))
        val paginatedQuery = querydsl!!.applyPagination(pageable, query)

        return PageImpl(
            paginatedQuery.fetch(),
            pageable,
            paginatedQuery.fetchCount()
        )
    }

    private fun eqUserId(userId: Long?, role: AccountRole?): BooleanExpression? {
        return if (userId == null || role == null) {
            null
        } else {
            if (role == AccountRole.RETAILER) {
                QOrderSheet.orderSheet.retailer.id.eq(userId)
            } else {
                QOrderSheet.orderSheet.wholesaler.id.eq(userId)
            }
        }
    }

    private fun eqDate(date: LocalDate?): BooleanExpression? {
        return if (date == null) {
            null
        } else {
            Expressions.dateTimeOperation(
                LocalDate::class.java, Ops.DateTimeOps.DATE,
                QOrderSheet.orderSheet.createdAt
            ).eq(date)
        }
    }

//    companion object {
//        private const val FIELD_DATE = "date"
//        private const val FIELD_FLOWER_TYPES_COUNT = "flowerTypesCount"
//        private const val FIELD_ORDERSHEETS_COUNT = "orderSheetsCount"
//    }

//    override fun getAllDailyOrderSheetsAggregates(
//        wholesalerId: Long,
//        fromDate: LocalDate,
//        toDate: LocalDate,
//        pageable: Pageable
//    ): Page<OrderSheetsAggregate> {
//        val orderSheet = QOrderSheet.orderSheet
//        val orderItem = QOrderItem.orderItem
//        val flower = QFlower.flower
//        val orderSheetReceipt = QOrderSheetReceipt.orderSheetReceipt
//
////        val dateFormatter = "DATE_FORMAT({0}, {1})"
////        val dateFormat = "%d-%m-%Y"
////
////        val formattedOrderSheetDate: DateTemplate<LocalDateTime> = Expressions.dateTemplate(
////            LocalDateTime::class.java,
////            dateFormatter,
////            orderSheet.createdAt,
////            dateFormat
////        )
//
////        val createdDate = Expressions.dateTimeOperation(
////            LocalDate::class.java, Ops.DateTimeOps.DATE,
////            QOrderSheet.orderSheet.createdAt
////        ).`as`(FIELD_DATE)
//
//        val createdDate = Expressions.dateTemplate(
//            LocalDate::class.java,
//            "date_trunc('day', {0})",
//            orderSheet.createdAt
//        )
//
//        val query = queryFactory.select(
//            Projections.constructor(
//                OrderSheetsAggregate::class.java,
//                orderSheet.createdAt.min().`as`(FIELD_DATE),
//                flower.flowerType.countDistinct().intValue().`as`(FIELD_FLOWER_TYPES_COUNT),
//                orderSheet.countDistinct().intValue().`as`(FIELD_ORDERSHEETS_COUNT),
//            )
//        )
//            .from(orderSheet)
////            .innerJoin(orderSheet.orderSheetReceipts, orderSheetReceipt)
//            .innerJoin(orderSheet.orderItems, orderItem)
//            .innerJoin(orderItem.flower, flower)
//            .where(eqUserId(userId = wholesalerId, role = AccountRole.WHOLESALER_EMPR), btwDate(fromDate, toDate))
//            .groupBy(createdDate)
////            .orderBy(createdDate.desc())
//
////        val paginatedQuery = querydsl!!.applyPagination(pageable, query)
//
//        return PageImpl(
//            query.fetch(),
//            pageable,
//            query.fetchCount(),
//        )
//    }
//
//    override fun getDailyOrderSheetsAggregate(wholesalerId: Long, date: LocalDate): OrderSheetsAggregate {
//        val orderSheet = QOrderSheet.orderSheet
//        val orderItem = QOrderItem.orderItem
//        val flower = QFlower.flower
//
//        val query = queryFactory.select(
//            Projections.constructor(
//                OrderSheetsAggregate::class.java,
//                orderSheet.createdAt.min().`as`(FIELD_DATE),
//                flower.flowerType.countDistinct().intValue().`as`(FIELD_FLOWER_TYPES_COUNT),
//                orderSheet.countDistinct().intValue().`as`(FIELD_ORDERSHEETS_COUNT),
//            )
//        )
//            .from(orderSheet)
//            .join(orderSheet.orderItems, orderItem)
//            .join(orderItem.flower, flower).fetchJoin()
//            .where(eqDate(date))
//
//        return query.fetchFirst()
//    }


}