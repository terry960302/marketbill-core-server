package kr.co.marketbill.marketbillcoreserver.domain.specs

import kr.co.marketbill.marketbillcoreserver.domain.entity.flower.Flower
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.CartItem
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.User
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Component
import javax.persistence.criteria.Join

@Component
class CartItemSpecs {
    companion object {
        fun hasWholesaler(): Specification<CartItem> {
            return Specification<CartItem> { root, query, builder ->
                val wholesaler: Join<CartItem, User> = root.join("wholesaler")
                wholesaler.isNotNull
            };
        }

        fun byRetailerId(retailerId: Long?): Specification<CartItem> {
            return Specification<CartItem> { root, query, builder ->
                if (retailerId == null) {
                    builder.conjunction()
                } else {
                    val retailer = root.join<CartItem, User>("retailer")
                    builder.equal(retailer.get<Long>("id"), retailerId)
                }
            }
        }

        fun byFlowerId(flowerId: Long?): Specification<CartItem> {
            return Specification<CartItem> { root, query, builder ->
                if (flowerId == null) {
                    builder.conjunction()
                } else {
                    val flower = root.join<CartItem, Flower>("flower")
                    builder.equal(flower.get<Long>("id"), flowerId)
                }
            }
        }

    }
}