package kr.co.marketbill.marketbillcoreserver.infrastructure.repository.order

import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import kr.co.marketbill.marketbillcoreserver.application.dto.request.GroupedCartItemCountDto
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.CartItem
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.QCartItem
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport

class CartItemRepositoryImpl : QuerydslRepositorySupport(CartItem::class.java), CartItemRepositoryCustom {

    @Autowired
    private lateinit var queryFactory: JPAQueryFactory

    override fun countTotalPaginatedCartItemsBySessionIds(sessionIds: List<Long>): List<GroupedCartItemCountDto> {
        val cartItem = QCartItem.cartItem

        val whereClause = cartItem.shoppingSession.id.`in`(sessionIds).and(
            cartItem.shoppingSession.id.isNotNull.and(
                cartItem.deletedAt.isNull.and(
                    cartItem.orderedAt.isNull
                )
            )
        )
        val query = queryFactory.select(
            Projections.constructor(
                GroupedCartItemCountDto::class.java,
                cartItem.shoppingSession.id.`as`("sessionId"),
                cartItem.id.countDistinct().`as`("count")
            )
        ).from(cartItem).where(whereClause).groupBy(cartItem.shoppingSession.id)

        return query.fetch()
    }

}