package kr.co.marketbill.marketbillcoreserver.cart.adapter.out.persistence.repository

import kr.co.marketbill.marketbillcoreserver.cart.application.port.outbound.CartRepository
import kr.co.marketbill.marketbillcoreserver.cart.domain.model.CartItem
import kr.co.marketbill.marketbillcoreserver.cart.domain.model.ShoppingSession
import kr.co.marketbill.marketbillcoreserver.cart.domain.vo.CartItemId
import kr.co.marketbill.marketbillcoreserver.cart.domain.vo.ShoppingSessionId
import kr.co.marketbill.marketbillcoreserver.flower.application.port.outbound.FlowerRepository
import kr.co.marketbill.marketbillcoreserver.flower.domain.vo.FlowerId
import kr.co.marketbill.marketbillcoreserver.user.application.port.outbound.UserRepository
import kr.co.marketbill.marketbillcoreserver.user.domain.vo.UserId
import org.springframework.stereotype.Repository

@Repository
class CartRepositoryImpl(
        private val cartItemCrudRepository: CartItemCrudRepository,
        private val shoppingSessionCrudRepository: ShoppingSessionCrudRepository,
        private val userRepository: UserRepository,
        private val flowerRepository: FlowerRepository
) : CartRepository {

    override fun saveCartItem(cartItem: CartItem): CartItem {
        val cartItemJpo = CartItem.toJpo(cartItem)
        val savedCartItemJpo = cartItemCrudRepository.save(cartItemJpo)

        return CartItem.fromJpo(
                savedCartItemJpo,
                cartItem.retailer,
                cartItem.wholesaler,
                cartItem.flower
        )
    }

    override fun findCartItemById(cartItemId: CartItemId): CartItem? {
        val cartItemJpo =
                cartItemCrudRepository.findById(cartItemId.value).orElse(null) ?: return null

        val retailer =
                userRepository.findById(UserId(cartItemJpo.retailerJpo!!.id!!))
                        ?: throw IllegalStateException("Retailer not found")

        val wholesaler =
                cartItemJpo.wholesalerJpo?.let { userRepository.findById(UserId.from(it.id!!)) }

        val flower =
                flowerRepository.findById(FlowerId(cartItemJpo.flowerJpo!!.id!!))
                        ?: throw IllegalStateException("Flower not found")

        return CartItem.fromJpo(cartItemJpo, retailer, wholesaler, flower)
    }

    override fun findCartItemsByRetailerId(retailerId: UserId): List<CartItem> {
        val cartItemJpos =
                cartItemCrudRepository.findByRetailerJpoAndDeletedAtIsNull(retailerId.value)

        val retailer =
                userRepository.findById(retailerId)
                        ?: throw IllegalStateException("Retailer not found")

        return cartItemJpos.mapNotNull {
            val wholesaler = it.wholesalerJpo?.let { userRepository.findById(UserId(it.id!!)) }

            val flower =
                    flowerRepository.findById(FlowerId(it.flowerJpo!!.id!!))
                            ?: return@mapNotNull null

            CartItem.fromJpo(it, retailer, wholesaler, flower)
        }
    }

    override fun deleteCartItem(cartItemId: CartItemId) {
        cartItemCrudRepository.deleteById(cartItemId.value)
    }

    override fun saveShoppingSession(shoppingSession: ShoppingSession): ShoppingSession {
        val shoppingSessionJpo = ShoppingSession.toJpo(shoppingSession)
        val savedShoppingSessionJpo = shoppingSessionCrudRepository.save(shoppingSessionJpo)

        return ShoppingSession.fromJpo(
                savedShoppingSessionJpo,
                shoppingSession.retailer,
                shoppingSession.wholesaler,
                shoppingSession.cartItems
        )
    }

    override fun findShoppingSessionByRetailerId(retailerId: UserId): ShoppingSession? {
        val shoppingSessionJpo =
                shoppingSessionCrudRepository.findByRetailerJpoAndDeletedAtIsNull(retailerId.value)
                        ?: return null

        val retailer =
                userRepository.findById(retailerId)
                        ?: throw IllegalStateException("Retailer not found")

        val wholesaler =
                shoppingSessionJpo.wholesalerJpo?.let { userRepository.findById(UserId(it.id!!)) }

        val cartItems = findCartItemsByRetailerId(retailerId)

        return ShoppingSession.fromJpo(shoppingSessionJpo, retailer, wholesaler, cartItems)
    }

    override fun findShoppingSessionById(shoppingSessionId: ShoppingSessionId): ShoppingSession? {
        val shoppingSessionJpo =
                shoppingSessionCrudRepository.findById(shoppingSessionId.value).orElse(null)
                        ?: return null

        val retailer =
                userRepository.findById(UserId(shoppingSessionJpo.retailerJpo!!.id!!))
                        ?: throw IllegalStateException("Retailer not found")

        val wholesaler =
                shoppingSessionJpo.wholesalerJpo?.let { userRepository.findById(UserId(it.id!!)) }

        val cartItems = findCartItemsByRetailerId(UserId(shoppingSessionJpo.retailerJpo!!.id!!))

        return ShoppingSession.fromJpo(shoppingSessionJpo, retailer, wholesaler, cartItems)
    }

    override fun findShoppingSessionsByRetailerIds(
            retailerIds: Set<UserId>
    ): Map<ShoppingSessionId, ShoppingSession> {
        val shoppingSessionJpos =
                shoppingSessionCrudRepository.findByRetailerJpoInAndDeletedAtIsNull(
                        retailerIds.map { it.value }
                )

        val retailers = userRepository.findByIds(retailerIds)

        return shoppingSessionJpos.associate { shoppingSessionJpo ->
            val retailerId = UserId(shoppingSessionJpo.retailerJpo!!.id!!)
            val retailer =
                    retailers[retailerId]
                            ?: throw IllegalStateException(
                                    "Retailer not found for id: ${retailerId.value}"
                            )

            val wholesaler =
                    shoppingSessionJpo.wholesalerJpo?.let {
                        userRepository.findById(UserId(it.id!!))
                    }

            val cartItems = findCartItemsByRetailerId(retailerId)

            val shoppingSession =
                    ShoppingSession.fromJpo(shoppingSessionJpo, retailer, wholesaler, cartItems)

            ShoppingSessionId(shoppingSessionJpo.id!!) to shoppingSession
        }
    }
}
