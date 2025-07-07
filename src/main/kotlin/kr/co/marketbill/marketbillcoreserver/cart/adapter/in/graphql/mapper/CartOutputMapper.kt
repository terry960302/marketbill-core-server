package kr.co.marketbill.marketbillcoreserver.cart.adapter.`in`.graphql.mapper

import java.time.LocalDate
import kr.co.marketbill.marketbillcoreserver.cart.domain.model.CartItem as DomainCartItem
import kr.co.marketbill.marketbillcoreserver.cart.domain.model.ShoppingSession as DomainShoppingSession
import kr.co.marketbill.marketbillcoreserver.flower.adapter.`in`.graphql.mapper.FlowerOutputMapper as GraphqlFlowerOutputMapper
import kr.co.marketbill.marketbillcoreserver.types.CartItem
import kr.co.marketbill.marketbillcoreserver.types.CartItemsOutput
import kr.co.marketbill.marketbillcoreserver.types.Flower
import kr.co.marketbill.marketbillcoreserver.types.FlowerGrade
import kr.co.marketbill.marketbillcoreserver.types.ShoppingSession
import kr.co.marketbill.marketbillcoreserver.user.adapter.`in`.graphql.mapper.UserOutputMapper as GraphqlUserOutputMapper
import kr.co.marketbill.marketbillcoreserver.user.application.result.UserResult
import org.springframework.stereotype.Component

@Component
class CartOutputMapper(
        private val userOutputMapper: GraphqlUserOutputMapper,
        private val flowerOutputMapper: GraphqlFlowerOutputMapper
) {

        fun toCartItemOutput(cartItem: DomainCartItem): CartItem {
                return CartItem(
                        id = cartItem.id?.value?.toInt() ?: 0,
                        retailer = userOutputMapper.toUser(UserResult.from(cartItem.retailer!!)),
                        wholesaler =
                                cartItem.wholesaler?.let {
                                        userOutputMapper.toUser(UserResult.from(it))
                                },
                        flower = mapToFlower(cartItem.flower!!),
                        quantity = cartItem.quantity.value,
                        grade = FlowerGrade.valueOf(cartItem.grade.name),
                        memo = cartItem.memo?.value,
                        orderedAt = cartItem.orderedAt?.toLocalDate(),
                        createdAt = cartItem.createdAt?.toLocalDate() ?: LocalDate.now(),
                        updatedAt = cartItem.updatedAt?.toLocalDate() ?: LocalDate.now(),
                        deletedAt = cartItem.deletedAt?.toLocalDate()
                )
        }

        fun toShoppingSessionOutput(shoppingSession: DomainShoppingSession): ShoppingSession {
                return ShoppingSession(
                        id = shoppingSession.id?.value?.toInt() ?: 0,
                        retailer =
                                userOutputMapper.toUser(
                                        UserResult.from(shoppingSession.retailer!!)
                                ),
                        wholesaler =
                                shoppingSession.wholesaler?.let {
                                        userOutputMapper.toUser(UserResult.from(it))
                                },
                        memo = shoppingSession.memo?.value,
                        cartItems = CartItemsOutput(resultCount = 0, items = emptyList()),
                        createdAt = shoppingSession.createdAt?.toLocalDate() ?: LocalDate.now(),
                        updatedAt = shoppingSession.updatedAt?.toLocalDate() ?: LocalDate.now(),
                        deletedAt = shoppingSession.deletedAt?.toLocalDate()
                )
        }

        private fun mapToFlower(
                flower: kr.co.marketbill.marketbillcoreserver.flower.domain.model.Flower
        ): Flower {
                return Flower(
                        id = flower.id?.let { it.value.toInt() } ?: 0,
                        flowerType =
                                flower.type.let {
                                        kr.co.marketbill.marketbillcoreserver.types.FlowerType(
                                                id = it.id?.let { it.value.toInt() } ?: 0,
                                                name = it.name,
                                                imgUrl = it.imgUrl
                                        )
                                },
                        name = flower.name,
                        images = flower.images.map { it.value },
                        flowerColor =
                                flower.color.let {
                                        kr.co.marketbill.marketbillcoreserver.types.FlowerColor(
                                                id = it.id?.let { it.value.toInt() } ?: 0,
                                                name = it.name
                                        )
                                },
                        biddingFlowers = emptyList()
                )
        }
}
