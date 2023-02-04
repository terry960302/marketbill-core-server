package kr.co.marketbill.marketbillcoreserver.domain.specs

import kr.co.marketbill.marketbillcoreserver.domain.entity.flower.Flower
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.CartItem
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.ShoppingSession
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.User
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Component
import javax.persistence.criteria.Join

@Component
class CartItemSpecs {
    companion object {

        fun byShoppingSessionIds(shoppingSessionIds: List<Long>): Specification<CartItem> {
            return Specification<CartItem> { root, query, builder ->
                if (shoppingSessionIds.isEmpty()) {
                    builder.conjunction()
                } else {
                    val shoppingSession = root.join<CartItem, ShoppingSession>("shoppingSession")
                    shoppingSession.get<Long>("id").`in`(shoppingSessionIds)
                }

            }
        }

        fun byShoppingSessionId(shoppingSessionId: Long?): Specification<CartItem> {
            return Specification<CartItem> { root, query, builder ->
                if (shoppingSessionId == null) {
                    builder.conjunction()
                } else {
                    val shoppingSession = root.join<CartItem, ShoppingSession>("shoppingSession")
                    builder.equal(shoppingSession.get<Long>("id"), shoppingSessionId)
                }

            }
        }

        fun hasWholesaler(): Specification<CartItem> {
            return Specification<CartItem> { root, query, builder ->
                val wholesaler: Join<CartItem, User> = root.join("wholesaler")
                wholesaler.isNotNull
            }
        }

        fun excludeId(cartItemId: Long?): Specification<CartItem> {
            return Specification<CartItem> { root, query, builder ->
                if (cartItemId == null) {
                    builder.conjunction()
                } else {
                    builder.notEqual(root.get<Long>("id"), cartItemId)
                }
            }
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

        fun byFlowerGrade(gradeKor: String?): Specification<CartItem> {
            return Specification<CartItem> { root, query, builder ->
                if (gradeKor == null) {
                    builder.conjunction()
                } else {
                    builder.equal(root.get<String>("grade"), gradeKor)
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