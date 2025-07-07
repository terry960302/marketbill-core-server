package kr.co.marketbill.marketbillcoreserver.legacy.infrastructure.repository.order

import com.querydsl.core.types.Ops
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.impl.JPAQueryFactory
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.OrderSheet
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.QOrderSheet
import kr.co.marketbill.marketbillcoreserver.shared.constants.AccountRole
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
}