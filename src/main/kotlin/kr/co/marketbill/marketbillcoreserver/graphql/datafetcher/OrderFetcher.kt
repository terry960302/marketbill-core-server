package kr.co.marketbill.marketbillcoreserver.graphql.datafetcher

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import kr.co.marketbill.marketbillcoreserver.DgsConstants
import kr.co.marketbill.marketbillcoreserver.constants.AccountRole
import kr.co.marketbill.marketbillcoreserver.constants.FlowerGrade
import kr.co.marketbill.marketbillcoreserver.domain.dto.OrderSheetsAggregate
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.CartItem
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.OrderItem
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.OrderSheet
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.ShoppingSession
import kr.co.marketbill.marketbillcoreserver.domain.entity.user.User
import kr.co.marketbill.marketbillcoreserver.security.JwtProvider
import kr.co.marketbill.marketbillcoreserver.service.CartService
import kr.co.marketbill.marketbillcoreserver.service.OrderService
import kr.co.marketbill.marketbillcoreserver.service.UserService
import kr.co.marketbill.marketbillcoreserver.types.*
import kr.co.marketbill.marketbillcoreserver.util.GqlDtoConverter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RequestHeader
import java.time.LocalDate
import java.util.Optional

@DgsComponent
class OrderFetcher {
    @Autowired
    private lateinit var cartService: CartService

    @Autowired
    private lateinit var orderService: OrderService

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var jwtProvider: JwtProvider

    val logger: Logger = LoggerFactory.getLogger(OrderFetcher::class.java)

    @Deprecated(message = "Replaced by getShoppingSession")
    @PreAuthorize("hasRole('RETAILER')")
    @DgsQuery(field = DgsConstants.QUERY.GetCartWholesaler)
    fun getCartWholesaler(
        @RequestHeader(value = JwtProvider.AUTHORIZATION_HEADER_NAME, required = true) authorization: String
    ): Optional<User> {
        val token = jwtProvider.filterOnlyToken(authorization)
        val userId: Long = jwtProvider.parseUserId(token)
        return cartService.getConnectedWholesalerOnCartItems(userId)
    }

    @Deprecated(message = "Replaced by getShoppingSession")
    @PreAuthorize("hasRole('RETAILER')")
    @DgsQuery(field = DgsConstants.QUERY.GetAllCartItems)
    fun getAllCartItems(
        @RequestHeader(value = JwtProvider.AUTHORIZATION_HEADER_NAME, required = true) authorization: String,
        @InputArgument pagination: PaginationInput?
    ): Page<CartItem> {
        val token = jwtProvider.filterOnlyToken(authorization)
        val pageable = GqlDtoConverter.convertPaginationInputToPageable(pagination)
        val userId: Long = jwtProvider.parseUserId(token)
        return cartService.getAllCartItems(userId, pageable)
    }

    @PreAuthorize("hasRole('RETAILER')")
    @DgsQuery(field = DgsConstants.QUERY.GetShoppingSession)
    fun getShoppingSession(
        @RequestHeader(value = JwtProvider.AUTHORIZATION_HEADER_NAME, required = true) authorization: String
    ): Optional<ShoppingSession> {
        val token = jwtProvider.filterOnlyToken(authorization)
        val userId: Long = jwtProvider.parseUserId(token)
        return cartService.getShoppingSession(userId)
    }

    @PreAuthorize("hasRole('RETAILER')")
    @DgsMutation(field = DgsConstants.MUTATION.UpdateShoppingSession)
    fun updateShoppingSession(
        @RequestHeader(value = JwtProvider.AUTHORIZATION_HEADER_NAME, required = true) authorization: String,
        @InputArgument input: UpdateShoppingSessionInput,
    ): ShoppingSession {
        val token = jwtProvider.filterOnlyToken(authorization)
        val userId: Long = jwtProvider.parseUserId(token)
        return cartService.updateShoppingSession(
            retailerId = userId,
            wholesalerId = input.wholesalerId?.toLong(),
            memo = input.memo
        )
    }


    // 공용
    @DgsQuery(field = DgsConstants.QUERY.GetOrderSheet)
    fun getOrderSheet(@InputArgument orderSheetId: Long): OrderSheet {
        return orderService.getOrderSheet(orderSheetId)
    }

    // 공용
    @DgsQuery(field = DgsConstants.QUERY.GetOrderSheets)
    fun getOrderSheets(
        @RequestHeader(value = JwtProvider.AUTHORIZATION_HEADER_NAME, required = false) authorization: Optional<String>,
        @InputArgument filter: DateFilterInput?,
        @InputArgument pagination: PaginationInput?
    ): Page<OrderSheet> {
        var userId: Long? = null
        var role: AccountRole? = null
        var date: LocalDate? = null
        val pageable = GqlDtoConverter.convertPaginationInputToPageable(pagination)

        if (authorization.isPresent) {
            val token = jwtProvider.filterOnlyToken(authorization.get())
            userId = jwtProvider.parseUserId(token)
            role = jwtProvider.parseUserRole(token)

            if (role == AccountRole.WHOLESALER_EMPE) {
                userId = userService.getConnectedEmployerId(userId)
            }
        }

        if (filter != null) {
            date = LocalDate.parse(filter.date)
        }

        return orderService.getOrderSheets(userId, role, date, pageable)
    }

    // 공용
    @DgsQuery(field = DgsConstants.QUERY.GetOrderItems)
    fun getOrderItems(
        @RequestHeader(value = JwtProvider.AUTHORIZATION_HEADER_NAME, required = false) authorization: Optional<String>,
        @InputArgument filter: DateFilterInput?,
        @InputArgument pagination: PaginationInput?
    ): Page<OrderItem> {
        var date: LocalDate? = null
        var userId: Long? = null
        var role: AccountRole? = null
        val pageable = GqlDtoConverter.convertPaginationInputToPageable(pagination)

        if (authorization.isPresent) {
            val token = jwtProvider.filterOnlyToken(authorization.get())
            userId = jwtProvider.parseUserId(token)
            role = jwtProvider.parseUserRole(token)

            if (role == kr.co.marketbill.marketbillcoreserver.constants.AccountRole.WHOLESALER_EMPE) {
                userId = userService.getConnectedEmployerId(userId)
            }
        }

        if (filter != null) {
            date = LocalDate.parse(filter.date)
        }

        return orderService.getOrderItems(userId, role, date, pageable)
    }

    // 도매상
    @PreAuthorize("hasRole('WHOLESALER_EMPR') or hasRole('WHOLESALER_EMPE')")
    @DgsQuery(field = DgsConstants.QUERY.GetAllDailyOrderSheetAggregates)
    fun getAllDailyOrderSheetAggregates(
        @RequestHeader(value = JwtProvider.AUTHORIZATION_HEADER_NAME, required = true) authorization: String,
        @InputArgument pagination: PaginationInput?
    ): Page<OrderSheetsAggregate> {
        val token = jwtProvider.filterOnlyToken(authorization)
        var userId = jwtProvider.parseUserId(token)
        val role = jwtProvider.parseUserRole(token)
        val pageable = GqlDtoConverter.convertPaginationInputToPageable(pagination, sortBy = "date")

        if (role == AccountRole.WHOLESALER_EMPE) {
            userId = userService.getConnectedEmployerId(userId)
        }

        return orderService.getAllDailyOrderSheetsAggregates(userId, pageable)
    }

    @PreAuthorize("hasRole('WHOLESALER_EMPR') or hasRole('WHOLESALER_EMPE')")
    @DgsQuery(field = DgsConstants.QUERY.GetDailyOrderSheetAggregate)
    fun getDailyOrderSheetAggregate(
        @RequestHeader(value = JwtProvider.AUTHORIZATION_HEADER_NAME, required = true) authorization: String,
        @InputArgument date: String?,
    ): Optional<OrderSheetsAggregate> {
        val token = jwtProvider.filterOnlyToken(authorization)
        var userId = jwtProvider.parseUserId(token)
        val role = jwtProvider.parseUserRole(token)

        if (role == AccountRole.WHOLESALER_EMPE) {
            userId = userService.getConnectedEmployerId(userId)
        }
        return orderService.getDailyOrderSheetsAggregate(userId, date)
    }

    @PreAuthorize("hasRole('WHOLESALER_EMPR') or hasRole('WHOLESALER_EMPE')")
    @DgsQuery(field = DgsConstants.QUERY.GetDailyOrderItems)
    fun getDailyOrderItems(
        @RequestHeader(value = JwtProvider.AUTHORIZATION_HEADER_NAME, required = true) authorization: String,
        @InputArgument filter: DailyOrderItemFilterInput?,
        @InputArgument pagination: PaginationInput?,
    ): Page<kr.co.marketbill.marketbillcoreserver.domain.entity.order.DailyOrderItem> {
        val token = jwtProvider.filterOnlyToken(authorization)
        var userId = jwtProvider.parseUserId(token)
        val role = jwtProvider.parseUserRole(token)
        var fromDate: LocalDate? = null
        var toDate: LocalDate? = null
        val pageable = GqlDtoConverter.convertPaginationInputToPageable(pagination)

        if (role == AccountRole.WHOLESALER_EMPE) {
            userId = userService.getConnectedEmployerId(userId)
        }

        if (filter != null) {
            if (filter.dateRange != null) {
                fromDate = LocalDate.parse(filter.dateRange.fromDate)
                toDate = LocalDate.parse(filter.dateRange.toDate)
            }
        }

        return orderService.getDailyOrderItems(userId, fromDate, toDate, pageable)
    }


    // mutation
    @PreAuthorize("hasRole('RETAILER')")
    @DgsMutation(field = DgsConstants.MUTATION.AddToCart)
    fun addToCart(
        @RequestHeader(value = JwtProvider.AUTHORIZATION_HEADER_NAME, required = true) authorization: String,
        @InputArgument input: AddToCartInput
    ): CartItem {
        val token = jwtProvider.filterOnlyToken(authorization)
        val userId: Long = jwtProvider.parseUserId(token)
        return cartService.addCartItem(
            retailerId = userId,
            flowerId = input.flowerId.toLong(),
            quantity = input.quantity,
            grade = FlowerGrade.valueOf(input.grade.toString())
        )
    }

    @PreAuthorize("hasRole('RETAILER')")
    @DgsMutation(field = DgsConstants.MUTATION.UpdateCartItem)
    fun updateCartItem(@InputArgument input: UpdateCartItemInput): CartItem {
        return cartService.updateCartItem(
            input.id.toLong(),
            input.quantity,
            FlowerGrade.valueOf(input.grade.toString())
        )
    }

    @PreAuthorize("hasRole('RETAILER')")
    @DgsMutation(field = DgsConstants.MUTATION.RemoveCartItem)
    fun removeCartItem(@InputArgument cartItemId: Long): CommonResponse {
        val removedCartItemId = cartService.removeCartItem(cartItemId)
        logger.info("[removeCartItem] removed $removedCartItemId")
        return CommonResponse(success = true)
    }

    @Deprecated(message = "Replaced by updateShoppingSession")
    @PreAuthorize("hasRole('RETAILER')")
    @DgsMutation(field = DgsConstants.MUTATION.UpsertWholesalerOnCartItems)
    fun upsertWholesalerOnCartItems(
        @RequestHeader(value = JwtProvider.AUTHORIZATION_HEADER_NAME, required = true) authorization: String,
        @InputArgument wholesalerId: Long
    ): List<CartItem> {
        val token = jwtProvider.filterOnlyToken(authorization)
        val retailerId = jwtProvider.parseUserId(token)
        return cartService.upsertWholesalerOnCartItems(retailerId, wholesalerId)
    }

    @PreAuthorize("hasRole('RETAILER')")
    @DgsMutation(field = DgsConstants.MUTATION.OrderCartItems)
    fun orderCartItems(
        @RequestHeader(value = JwtProvider.AUTHORIZATION_HEADER_NAME, required = true) authorization: String,
    ): OrderSheet {
        val token = jwtProvider.filterOnlyToken(authorization)
        val retailerId = jwtProvider.parseUserId(token)
        return orderService.orderAllCartItems(retailerId)
    }

    @PreAuthorize("hasRole('RETAILER')")
    @DgsMutation(field = DgsConstants.MUTATION.RemoveOrderSheet)
    fun removeOrderSheet(@InputArgument orderSheetId: Long): CommonResponse {
        val removedOrderSheetId = orderService.removeOrderSheet(orderSheetId).toInt()
        logger.info("[removeOrderSheet] removed $removedOrderSheetId")
        return CommonResponse(success = true)

    }

    @PreAuthorize("hasRole('WHOLESALER_EMPR') or hasRole('WHOLESALER_EMPE')")
    @DgsMutation(field = DgsConstants.MUTATION.UpdateOrderItemsPrice)
    fun updateOrderItemsPrice(@InputArgument items: List<OrderItemPriceInput>): List<OrderItem> {
        return orderService.updateOrderItemsPrice(items)
    }

    @PreAuthorize("hasRole('WHOLESALER_EMPR') or hasRole('WHOLESALER_EMPE')")
    @DgsMutation(field = DgsConstants.MUTATION.UpdateDailyOrderItemsPrice)
    fun updateDailyOrderItemsPrice(@InputArgument items: List<OrderItemPriceInput>): List<kr.co.marketbill.marketbillcoreserver.domain.entity.order.DailyOrderItem> {
        return orderService.updateDailyOrderItemsPrice(items)
    }

    @PreAuthorize("hasRole('WHOLESALER_EMPR')")
    @DgsMutation(field = DgsConstants.MUTATION.IssueOrderSheetReceipt)
    fun issueOrderSheetReceipt(@InputArgument orderSheetId: Long): kr.co.marketbill.marketbillcoreserver.domain.entity.order.OrderSheetReceipt {
        return orderService.issueOrderSheetReceipt(orderSheetId)
    }


    @DgsMutation(field = DgsConstants.MUTATION.OrderBatchCartItems)
    fun orderBatchCartItems(): CommonResponse {
        cartService.orderBatchCartItems()
        return CommonResponse(success = true)
    }
}