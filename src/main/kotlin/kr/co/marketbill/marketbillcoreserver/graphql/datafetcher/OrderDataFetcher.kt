package kr.co.marketbill.marketbillcoreserver.graphql.datafetcher

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.InputArgument
import kr.co.marketbill.marketbillcoreserver.DgsConstants
import kr.co.marketbill.marketbillcoreserver.constants.DEFAULT_PAGE
import kr.co.marketbill.marketbillcoreserver.constants.DEFAULT_SIZE
import kr.co.marketbill.marketbillcoreserver.constants.FlowerGrade
import kr.co.marketbill.marketbillcoreserver.data.entity.order.CartItem
import kr.co.marketbill.marketbillcoreserver.security.JwtProvider
import kr.co.marketbill.marketbillcoreserver.service.CartService
import kr.co.marketbill.marketbillcoreserver.types.AddToCartInput
import kr.co.marketbill.marketbillcoreserver.types.PaginationInput
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.RequestHeader

@DgsComponent
class OrderDataFetcher {
    @Autowired
    private lateinit var cartService: CartService

    @Autowired
    private lateinit var jwtProvider: JwtProvider

    @DgsData(parentType = DgsConstants.QUERY.TYPE_NAME, field = DgsConstants.QUERY.GetAllCartItems)
    fun getAllCartItems(
        @RequestHeader("Authorization") authorization: String,
        @InputArgument pagination: PaginationInput?
    ): Page<CartItem> {
        var pageable: Pageable = PageRequest.of(DEFAULT_PAGE, DEFAULT_SIZE)
        if (pagination != null) {
            pageable = PageRequest.of(pagination.page!!, pagination.size!!)
        }
        val token = jwtProvider.filterOnlyToken(authorization)
        val userId: Long = jwtProvider.parseUserId(token)
        return cartService.getAllCartItems(userId, pageable)
    }

    @DgsData(parentType = DgsConstants.MUTATION.TYPE_NAME, field = DgsConstants.MUTATION.AddToCart)
    fun addToCart(
        @RequestHeader("Authorization") authorization: String,
        @InputArgument input: AddToCartInput
    ): CartItem {
        val token = jwtProvider.filterOnlyToken(authorization)
        val userId: Long = jwtProvider.parseUserId(token)
        return cartService.addToCart(userId, input.flowerId.toLong(), input.quantity, FlowerGrade.valueOf(input.grade))
    }

    @DgsData(parentType = DgsConstants.MUTATION.TYPE_NAME, field = DgsConstants.MUTATION.RemoveCartItem)
    fun removeCartItem(@InputArgument cartItemId: Long): Long {
        return cartService.removeCartItem(cartItemId)
    }
}