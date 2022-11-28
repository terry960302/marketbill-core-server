package kr.co.marketbill.marketbillcoreserver.domain.specs

import kr.co.marketbill.marketbillcoreserver.domain.entity.flower.Flower
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.CartItem
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.User
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Component
import javax.persistence.criteria.Join

@Component
class CartItemSpecs {
    companion object{
        fun hasWholesaler(): Specification<CartItem> {
            return Specification<CartItem> { root, query, builder ->
                val wholesaler : Join<CartItem, User> = root.join("wholesaler")
                wholesaler.isNotNull
            };
        }

    }
}