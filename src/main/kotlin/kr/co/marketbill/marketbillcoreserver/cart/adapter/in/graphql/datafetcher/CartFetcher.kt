package kr.co.marketbill.marketbillcoreserver.cart.adapter.`in`.graphql.datafetcher

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import kr.co.marketbill.marketbillcoreserver.cart.adapter.`in`.graphql.mapper.CartOutputMapper
import kr.co.marketbill.marketbillcoreserver.cart.application.command.*
import kr.co.marketbill.marketbillcoreserver.cart.application.service.CartService
import kr.co.marketbill.marketbillcoreserver.cart.domain.vo.CartItemId
import kr.co.marketbill.marketbillcoreserver.cart.domain.vo.Memo
import kr.co.marketbill.marketbillcoreserver.cart.domain.vo.Quantity
import kr.co.marketbill.marketbillcoreserver.flower.domain.vo.FlowerId
import kr.co.marketbill.marketbillcoreserver.shared.adapter.`in`.graphql.context.getUserIdFromContext
import kr.co.marketbill.marketbillcoreserver.types.*
import kr.co.marketbill.marketbillcoreserver.user.domain.vo.UserId
import org.springframework.stereotype.Component

@DgsComponent
class CartFetcher(
    private val cartService: CartService,
    private val cartOutputMapper: CartOutputMapper
) {

    @DgsQuery
    fun shoppingSession(): ShoppingSession? {
        val userId = getUserIdFromContext()
        val command = FindShoppingSessionCommand(retailerId = userId)
        return cartService.findShoppingSession(command)?.let { cartOutputMapper.toShoppingSessionOutput(it) }
    }

    @DgsMutation
    fun addToCart(@InputArgument input: AddToCartInput): CartItem {
        val userId = getUserIdFromContext()
        val command = AddToCartCommand(
            retailerId = userId,
            flowerId = FlowerId(input.flowerId.toLong()),
            quantity = Quantity(input.quantity),
            grade = kr.co.marketbill.marketbillcoreserver.flower.domain.vo.FlowerGrade.valueOf(input.grade.name),
            memo = input.memo?.let { Memo(it) }
        )
        val cartItem = cartService.addToCart(command)
        return cartOutputMapper.toCartItemOutput(cartItem)
    }

    @DgsMutation
    fun removeCartItem(@InputArgument cartItemId: Int): CommonResponse {
        val command = RemoveCartItemCommand(cartItemId = CartItemId(cartItemId.toLong()))
        cartService.removeCartItem(command)
        return CommonResponse(success = true)
    }

    @DgsMutation
    fun updateCartItem(@InputArgument input: UpdateCartItemInput): CartItem {
        val command = UpdateCartItemCommand(
            id = CartItemId(input.id.toLong()),
            quantity = Quantity(input.quantity),
            grade = kr.co.marketbill.marketbillcoreserver.flower.domain.vo.FlowerGrade.valueOf(input.grade.name),
            memo = input.memo?.let { Memo(it) }
        )
        val cartItem = cartService.updateCartItem(command)
        return cartOutputMapper.toCartItemOutput(cartItem)
    }

    @DgsMutation
    fun updateShoppingSession(@InputArgument input: UpdateShoppingSessionInput): ShoppingSession {
        val userId = getUserIdFromContext()
        val command = UpdateShoppingSessionCommand(
            retailerId = userId,
            wholesalerId = input.wholesalerId?.let { UserId(it.toLong()) },
            memo = input.memo?.let { Memo(it) }
        )
        val shoppingSession = cartService.updateShoppingSession(command)
        return cartOutputMapper.toShoppingSessionOutput(shoppingSession)
    }
}
