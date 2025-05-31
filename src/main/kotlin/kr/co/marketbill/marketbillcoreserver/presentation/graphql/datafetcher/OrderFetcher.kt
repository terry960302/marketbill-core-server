package kr.co.marketbill.marketbillcoreserver.presentation.graphql.datafetcher

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import com.netflix.graphql.types.errors.ErrorType
import kr.co.marketbill.marketbillcoreserver.DgsConstants
import kr.co.marketbill.marketbillcoreserver.application.dto.response.OrderSheetsAggregate
import kr.co.marketbill.marketbillcoreserver.application.service.cart.CartService
import kr.co.marketbill.marketbillcoreserver.application.service.order.OrderService
import kr.co.marketbill.marketbillcoreserver.application.service.user.UserService
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.CartItem
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.CustomOrderItem
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.OrderItem
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.OrderSheet
import kr.co.marketbill.marketbillcoreserver.domain.entity.order.ShoppingSession
import kr.co.marketbill.marketbillcoreserver.infrastructure.security.JwtProvider
import kr.co.marketbill.marketbillcoreserver.shared.constants.AccountRole
import kr.co.marketbill.marketbillcoreserver.shared.constants.CustomErrorCode
import kr.co.marketbill.marketbillcoreserver.shared.constants.FlowerGrade
import kr.co.marketbill.marketbillcoreserver.shared.exception.CustomException
import kr.co.marketbill.marketbillcoreserver.shared.util.GqlDtoConverter
import kr.co.marketbill.marketbillcoreserver.types.*
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

    @PreAuthorize("hasRole('RETAILER')")
    @DgsData.List(
        DgsData(parentType = DgsConstants.QUERY.TYPE_NAME, field = DgsConstants.QUERY.GetShoppingSession),
        DgsData(parentType = DgsConstants.QUERY.TYPE_NAME, field = DgsConstants.QUERY.ShoppingSession)
    )
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
    @DgsData.List(
        DgsData(parentType = DgsConstants.QUERY.TYPE_NAME, field = DgsConstants.QUERY.GetOrderSheet),
        DgsData(parentType = DgsConstants.QUERY.TYPE_NAME, field = DgsConstants.QUERY.OrderSheet),
    )
    fun getOrderSheet(@InputArgument orderSheetId: Long): OrderSheet {
        return orderService.getOrderSheet(orderSheetId)
    }

    // 공용
    @DgsData.List(
        DgsData(parentType = DgsConstants.QUERY.TYPE_NAME, field = DgsConstants.QUERY.GetOrderSheets),
        DgsData(parentType = DgsConstants.QUERY.TYPE_NAME, field = DgsConstants.QUERY.OrderSheets),
    )
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
    @DgsData.List(
        DgsData(parentType = DgsConstants.QUERY.TYPE_NAME, field = DgsConstants.QUERY.GetOrderItems),
        DgsData(parentType = DgsConstants.QUERY.TYPE_NAME, field = DgsConstants.QUERY.OrderItems),
    )
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

            if (role == AccountRole.WHOLESALER_EMPE) {
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
    @DgsData.List(
        DgsData(parentType = DgsConstants.QUERY.TYPE_NAME, field = DgsConstants.QUERY.GetAllDailyOrderSheetAggregates),
        DgsData(parentType = DgsConstants.QUERY.TYPE_NAME, field = DgsConstants.QUERY.DailyOrderSheetAggregates),
    )
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
    @DgsData.List(
        DgsData(parentType = DgsConstants.QUERY.TYPE_NAME, field = DgsConstants.QUERY.GetDailyOrderSheetAggregate),
        DgsData(parentType = DgsConstants.QUERY.TYPE_NAME, field = DgsConstants.QUERY.DailyOrderSheetAggregate),
    )
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
    @DgsData.List(
        DgsData(parentType = DgsConstants.QUERY.TYPE_NAME, field = DgsConstants.QUERY.GetDailyOrderItems),
        DgsData(parentType = DgsConstants.QUERY.TYPE_NAME, field = DgsConstants.QUERY.DailyOrderItems),
    )
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

    @PreAuthorize("hasRole('WHOLESALER_EMPR') or hasRole('WHOLESALER_EMPE')")
    @DgsMutation(field = DgsConstants.MUTATION.UpsertCustomOrderItems)
    fun upsertCustomOrderItems(@InputArgument input: UpsertCustomOrderItemsInput): List<CustomOrderItem> {
        val filteredInput: List<CustomOrderItemInput> = input.items.filter {
            it.grade != null ||
                    it.price != null ||
                    it.quantity != null ||
                    !it.flowerName.isNullOrBlank() ||
                    !it.flowerTypeName.isNullOrBlank()
        }
        if (filteredInput.isEmpty()) {
            throw CustomException(
                message = "All custom_order_items are composed of empty objects.",
                errorType = ErrorType.BAD_REQUEST,
                errorCode = CustomErrorCode.INVALID_DATA
            )
        }
        return orderService.upsertCustomOrderItems(input.orderSheetId.toLong(), filteredInput)
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